package pandemist

import scala.collection.mutable.Map

class Action(input: String) {

  private val commandText = input.trim.toLowerCase
  private val verb = commandText.takeWhile(_ != ' ')
  private val modifiers = commandText.drop(verb.length).trim

  /** Each command will return a tuple of the status and the returned report,
    * the status will indicate whether the command is valid or not.
    * If command is invalid due to user misspelled or mistyped the command, they will
    * have another chance to type the command, without having to lose a turn. */
  def execute(actor: Pandemist): (Boolean, String) = {
    this.verb match {
      case "move" => actor.move(this.modifiers)
      case "report" => actor.report(this.modifiers)
      case "recruit" | "hire" => actor.recruit(this.modifiers)
      case "assign" => (new Action(this.modifiers)).execute(actor)
      case "analyze" | "analyst" => actor.analyze(this.modifiers)
      case "research" | "researcher" => actor.research()
      case "treat" | "doctor" => actor.treat(this.modifiers)
      case "lockdown" | "quarantinist" => actor.lockdown(this.modifiers)
      case "rest" | "idle" | "p" => (true, "You do nothing")
      case "help" => (false, this.help)
      case "manual" => (false, this.manual)
      case "map" => (false, this.map(actor.regions))
      case "status" => (false, actor.fullStatus)
      case "quit" => (false, actor.quit())
      case "restart" => actor.restart()
      case _ => (false, "Unknown command: \"" + this.verb + "\".")
    }
  }


  /** Helper function to format the region label for the map function */
  private def format(region: Region) = {
    if (region.isInfected) "!!"
    else if (region.isDoomed) "XX"
    else region.regionCode
  }

  /** Create a map visualation of the game */
  private def map(regions: Map[String, Region]) = {
    val a = this.format(regions("north america"))
    val b = this.format(regions("south america"))
    val c = this.format(regions("europe"))
    val d = this.format(regions("africa"))
    val e = this.format(regions("middle east"))
    val f = this.format(regions("south asia"))
    val g = this.format(regions("east asia"))
    val h = this.format(regions("southeast asia"))
    val i = this.format(regions("oceania"))
    raw"""
________________________________________________________________________
|               ,_   .  ._. _.  .                                       |
|           , _-\','|~\~      ~/      ;-'_   _-'     ,;_;_,    ~~-      |
|  /~~-\_/-'~'--' \~~| ',    ,'      /  / ~|-_\_/~/~      ~~--~~~~'--_  |
|  /              ,/'-/~ '\ ,' _  , '|,'|~                   ._/-, /~   |
|  ~/-'~\_,       '-,| '|. '   ~  ,\ /'~                /    /_  /~     |
|.-~      '|        '',\~|\       _\~ $c  ,_  ,               /|
|          '\  $a     /'~        |_/~\\,-,~  \ "        $g ,_,/ |
|           |       /            ._-~'\_ _~| $e           \ ) /
|            \   __-\           '/      ~ |\  \_   $f      /  ~
|  .,         '\ |,  ~-_      - |          \\_' ~|  / \ \~ ,            |
|               ~-_'  _;       '\      $d   '-,   \,'  \/  $h
|                '\_,~'\_       \_ _,       /'    '  |, /|'             |
|                  /     \_       ~ |      /         \  ~'; -,_.        |
|                  |       ~\        |    |  ,        '-_, ,; ~ ~\      |
|                   \, $b   /        \    / /|            ,-, ,   -,
|                    |    ,/          |  |' |/          ,-   ~ \   '.   |
|                   ,|   ,/           \ ,/              \   $i   |
|                   /    |             ~                 -~~-, /   _    |
|                   |  ,-'                                    ~    /    |
|                   / ,'                                      ~         |
|                   ',|  ~                                              |
|                     ~'                                                |
|_______________________________________________________________________|
"""
  }

  private val help =
    """
Commands:
    0/ help:
      -> Show this message, this won't cost a turn

    1/ manual:
      -> Show the full manual, this won't cost a turn

    2/ quit:
      -> Exit the game

    3/ move "region's name"
      -> Move to a new region, their exact names are:

          "north america" "south america" "africa" "europe" "middle east"
          "south asia" "east asia" "southeast asia" "oceania"

      Ex: move south asia

    4/ report "region's name"
       report all
      -> Show the number of infected people in that region. This is only available when you have
         an analyst assigned to that region. If no region is provided, it's will
         report all possible regions.

      Ex: report north america

    5/ recruit / hire + "specialist's name"
      -> Hire a specialist from the region that you are in

      Ex: hire james
          recruit emma

    6/ assign + "specialist" + "region's name"
      -> Assign or use specialist's ability in a region. Possible choice for "specialist" are:
          "analyst" "doctor" "quarantinist" "researcher"

      Ex: assign analyst south america
          assign doctor europe
          assign quarantinist africa
          assign researcher

      -> You can also execute these command directly to achieve the same results:
      Ex: analyze south america
          treat europe
          lockdown africa
          research

    7/ map
      -> Show the map of the world, this won't cost a turn

    8/ status
      -> Show the status of your specialists, this won't cost a turn

    8/ rest / idle
      -> You do nothing

    9/ restart
      -> Restart the game

"""

  private val manual =
"""
  ___________________________________________________________________________
  |                                The Manual                               |
  |_________________________________________________________________________|

Context:
  | It's the year 2020, a sudden outbreak of a mysterious disease was reported in Eastern
  | Asia. It's spreading rapidly across the population but weirdly, have no symptoms
  | You were informed by the US intelligence that it's a biological weapon that was
  | accidentally released due to a careless handling. It was biologically designed
  | to only activate its letality and become deadly after a predetermined duration,
  | which is, in this case, exactly 1 year. You was the designated pandemist, who
  | will be the commander in chief of this operation to eradicate this disease.

Start:
  | You will start the operation having no specialists at your dispense,
  | You will need to find these specialist by go to each region and recruit them.
  | It's is advisable to find and recruit analyst first.

Specialist:
  | There are a total of five specialist class, each with their own unique abilities:

      + Analysts:
      | They are investigators and medical examiers who will determine the amount of infected patients
      | in a region and report back the numbers to you. They are also experts at examining
      | the virus and will slowly generate research points that is *very* important
      | later on for treating the patients and developing a cure. Assigning which analyst to a region is
      | done automatically, you will only need to choose which region. You cannot reassign an analyst.

      + Doctors:
      | They are the ones to call when it's coming to treating the patients. However without
      | a cure, they can only conduct medical operations that somewhat reduce the numbers of
      | infected temporarily. You will still need a cure to win. Also, you will need to have at least
      | 35% of the cure developed before any mitigation operations can be conducted in a region. The
      | more research points you have, the more effective is the operations. They also have cooldowns. The process
      | of determining which doctor is availabe to do the treatment will be done automatically.

      + Researchers:
      | They are the one who will develop the cure. The more research points you have, the faster is
      | the progress. You can also have multiple researchers and further increase the research rate.

      + Quarantinists:
      | They are the experts of isolating the population and stoping the virus's spread. When they are assigned to a
      | region, they will completely blocking that region from "bursting" a.k.a, spreading the virus to
      | its neighboring regions. A region which is occupied by a quarantinist will have the infection rate
      | dramatically reduced. The duration however, is not infinite. When the duration is over, the quarantinist exits
      | the region.

      + Pilots:
      | You can also find pilots scattered across the world. These pilots can send any specialists
      | to any region on the world, effectively allow you to place analysts, doctors and quarantinists
      | anywhere regardless of your location. You can also move to any region on the world if you have a pilot.
      | However, each individuals pilots have cooldowns. You can have multiple pilots and somewhat mitigate this limitations.
      | Choosing which pilots is done automatically.

Objectives:
  | Your goal is to develop the cure as fast as possible or eradicate the virus completely.
  | You will lose if the virus infect more than 99% of the poulation of all region.

  | You are the leader, the captain, the commander in chief of this operation.
  | The world is on your shoulder.
  | Every decision matters. So be patient.
  | Every commands you issues will decide the lives of millions.
  | Act swiftly. The virus waits noone.
  | Be smart. Plan your stategy.
  | Let the world see your leadership in action...

"""

  /** Returns a textual description of the action object, for debugging purposes. */
  override def toString = this.verb + " (modifiers: " + this.modifiers + ")"
}

