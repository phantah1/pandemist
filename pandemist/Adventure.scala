package pandemist

import scala.collection.mutable.Map

class Adventure {
  val title = "Pandemist"

  var regions = Map[String, Region]()

  /** The number of turns that have passed since the start of the game. */
  var turnCount = 0

  /** The maximum number of turns that this adventure game allows before time runs out. */
  val timeLimit = 73 /** 73 is approximately a year if one turn is equivalent to 5 in-game days */

  var player: Pandemist = new Pandemist(new Region("", "", "", 0), this)

  def start() = {

    val northAmerica = new Region("North America", "NA", "", 579024000, 2)
    val southAmerica = new Region("South America", "SA", "", 423581078, 2)
    val europe = new Region("Europe", "EU", "", 746419440, 1.5)
    val africa = new Region("Africa", "AF", "", 1275920972, 1.75)
    val middleEast = new Region("Middle East", "ME", "", 371000000, 1.5)
    val southAsia = new Region("South Asia", "SAs", "", 1947628100, 2)
    val eastAsia = new Region("East Asia", "EAs", "", 1680073720, 1.75)
    val southEastAsia = new Region("Southeast Asia", "SEA", "", 655298044, 1.5)
    val oceania = new Region("Oceania", "OC", "", 41570842, 1.5)

    regions = Map[String, Region](
      northAmerica.name.toLowerCase -> northAmerica,
      southAmerica.name.toLowerCase -> southAmerica,
      europe.name.toLowerCase -> europe,
      africa.name.toLowerCase -> africa,
      middleEast.name.toLowerCase -> middleEast,
      southAsia.name.toLowerCase -> southAsia,
      eastAsia.name.toLowerCase -> eastAsia,
      southEastAsia.name.toLowerCase -> southEastAsia,
      oceania.name.toLowerCase -> oceania
    )

    player = new Pandemist(northAmerica, this)

    northAmerica.setNeighbors(Vector("europe" -> europe, "south america" -> southAmerica, "east asia" -> eastAsia, "oceania" -> oceania))
    southAmerica.setNeighbors(Vector("africa" -> africa, "north america" -> northAmerica))
    europe.setNeighbors(Vector("north america" -> northAmerica, "africa" -> africa, "middle east" -> middleEast))
    africa.setNeighbors(Vector("europe" -> europe, "middle east" -> middleEast, "south america" -> southAmerica))
    middleEast.setNeighbors(Vector("africa" -> africa, "europe" -> europe, "south asia" -> southAsia, "east asia" -> eastAsia))
    southAsia.setNeighbors(Vector("middle east" -> middleEast, "east asia" -> eastAsia, "southeast asia" -> southEastAsia))
    eastAsia.setNeighbors(Vector("south asia" -> southAsia, "north america" -> northAmerica, "middle east" -> middleEast, "southeast asia" -> southEastAsia))
    southEastAsia.setNeighbors(Vector("east asia" -> eastAsia, "south asia" -> southAsia, "oceania" -> oceania))
    oceania.setNeighbors(Vector("southeast asia" -> southEastAsia, "north america" -> northAmerica))

    /** Place specialists on the map. Specialists are characters that have unique abilities
      * to help you fight against this pandemic. You can recruit them, assign them to region
      * to do jobs such as treating patients, enforce lockdown, develop a cure or analyze a region,
      * etc,... */

    northAmerica.setSpecialist(Vector(
      new Pilot("John"),
      new Researcher("James", player),
      new Doctor("Elsie", player),
      new Analyst("Emma"),
      new Quarantinist("Kate")
    ))

    southAmerica.setSpecialist(Vector(
      new Researcher("Lopez", player),
      new Analyst("Silva"),
      new Analyst("Roy"),
    ))

    europe.setSpecialist(Vector(
      new Researcher("Henry", player),
      new Researcher("Sofia", player),
    ))

    africa.setSpecialist(Vector(
      new Pilot("Leo"),
      new Researcher("Muhammad", player),
    ))

    middleEast.setSpecialist(Vector(
      new Analyst("Khalid"),
      new Quarantinist("Emily")
    ))

    eastAsia.setSpecialist(Vector(
      new Doctor("Ayako", player),
      new Doctor("Kwang", player),
    ))

    southAsia.setSpecialist(Vector(
      new Researcher("Aashi", player),
      new Researcher("Rajat", player),
    ))

    southEastAsia.setSpecialist(Vector(
      new Analyst("Hoang"),
      new Analyst("Anada"),
      new Analyst("Li"),
    ))

    oceania.setSpecialist(Vector(
      new Pilot("Thomas"),
      new Researcher("Ella", player),
      new Analyst("William"),
    ))

    /** Place 100 patient zero in East Asia */
    eastAsia.setInfected(100.0)
  }

  this.start()

  /** Determines if the adventure is complete, that is, if the player has won.
    * You win if you finished developing the cure for the disease or totally eradicated it */
  def isWin = this.player.curePercentageProgress >= 100.0 || this.regions.forall(_._2.percentInfected == 0)

  /** Determines if the adventure is complete, that is, if the player has won.
    * You lose if the disease infect over 90 percent of all region or you reach the time limit */
  def isLost = this.regions.forall(_._2.percentInfected > 99.0) || this.turnCount == this.timeLimit

  def isOver = this.isWin || this.isLost || this.player.hasQuit

  /** All regions update their infection numbers and report back */
  private def regionsUpdate() = this.regions.map(_._2.update()).filter(_.nonEmpty).mkString("\n")

  /** All hired specialists update their cooldown timer and report back events */
  private def specialistsUpdate() = player.specialists.map {
    case specialist: Specialist with CoolDown => specialist.playTurn()
    case _ => ""
  }.filter(_.nonEmpty).mkString("\n")


  /** Play a turn by executing the given in-game command, such as "move east asia". Returns a textual
    * report of what happened, or an error message if there is a error. In the latter
    * case, no turns elapse. */
  var hist = " "
  def playTurn(command: String) = {
    this.hist += command + "\n"
    val action = new Action(command)
    val (status, actionReport) = action.execute(this.player)

    var turnReport = "Command Report\n" + "-" * 12 + "\n" + actionReport + "\n"
    if (status) {
      this.turnCount += 1
      turnReport += "\nRegion Report\n" + "-" * 13 + "\n" + this.regionsUpdate() + "\nSpecialists Report\n" + "-" * 18 + "\n" + this.specialistsUpdate() + "\n"
    }
    turnReport
  }


  /** Returns a message that is to be displayed to the player at the beginning of the game. */
  def welcomeMessage =
    """
      _________________________________________________________________________________________
      | Welcome to the Pandemist !!!                                                          |
      |_______________________________________________________________________________________|
      | It's the year 2020, a sudden outbreak of a mysterious disease was reported in Eastern |
      | Asia. It's spreading rapidly across the population but weirdly, have no symptoms      |
      | You were informed by the US intelligence that it's a biological weapon that was       |
      | accidentally released due to a careless handling. It was biologically designed        |
      | to only activate its letality and become deadly after a predetermined duration,       |
      | which is, in this case, exactly 1 year. You was the designated pandemist, who         |
      | will be the commander in chief of this operation to save the world from the virus     |
      | Objectives: develop a cure as fast as possible or eradicate the disease.              |
      | You will lose if you run out of time or the virus infect over 99% of all region       |
      |_______________________________________________________________________________________|
    """


  /** Returns a message that is to be displayed to the player at the end of the game. The message
    * will be different depending on whether or not the player has completed their quest. */
  def goodbyeMessage = {
    println(this.hist)
    if (this.isWin)
      "Congratuation!! You have saved the world from the pandemic !!!"
    else if (this.isLost)
      "The world slowly crumble around you..." + (if(this.turnCount == this.timeLimit) "Is too late ..." else "Everyone is infected...")
    else // game over due to player quitting
      "You give up and resigned..."
  }
}

