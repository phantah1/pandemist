### Manual

#### Context
It's the year 2020, a sudden outbreak of a mysterious disease was reported in Eastern
Asia. It's spreading rapidly across the population but weirdly, have no symptoms.
You were informed by the US intelligence that it's a biological weapon that was
accidentally released due to careless handlings. It was biologically designed
to only activate its lethality and become deadly after a predetermined duration,
which is, in this case, exactly 1 year. You was the designated pandemist, who
will be the commander in chief of this operation to eradicate the disease.

#### Start
You will start the operation having no specialists at your dispense,
You will need to find these specialist by go to each region and recruit them.
It's is advisable to find and recruit analyst first.

#### Specialists
 There are a total of five specialist class, each with their own unique abilities:

+ **Analysts**:
 They are investigators and medical examiers who will determine the amount of infected patients
 in a region and report the numbers back to you. They are also experts at examining
 the virus and will slowly generate research points that is **_very important_**
 later on for treating the patients and developing a cure. Assigning which analyst to a region is
 done automatically, you will only need to choose which region. You cannot reassign an analyst.

+ **Doctors**:
 They are the ones to treat the patients. But without a cure, they can only conduct medical operations 
 that somewhat reduce the numbers of infected temporarily. You will still need a cure to win. Also,
 you will need to have at least 20% of the cure developed before any mitigation operations can be conducted.
 The more research points you have, the more effective is the treatments. They also have cooldowns. 
 The process of determining which doctor is availabe to do the treatment will be done automatically. You
 only need to choose which region.

+ **Researchers**:
 They are the one who will develop the cure. The more research points you have collected, the faster is
 the progress. You can also hire multiple researchers to further increase the research rate.

+ **Quarantinists**:
 They are the experts of isolating the population and stoping the virus's spread. When they are assigned to a
 region, they will completely blocking that region from "bursting" a.k.a, spreading the virus to
 its neighboring regions. A region which is occupied by a quarantinist will have the infection rate
 dramatically reduced. The duration however, is not infinite. When the duration is over, the quarantinist exits
 the region.

+ **Pilots**:
 You can also find pilots scattered across the world. These pilots can send any specialists
 to any region on the world, effectively allow you to place analysts, doctors and quarantinists
 anywhere regardless of your location. You can also move to any region on the world if you have a pilot.
 However, each individuals pilots have cooldowns. You can have multiple pilots and somewhat mitigate this limitations.
 Choosing which pilots is done automatically.

#### Objectives
Your goal is to develop the cure as fast as possible or eradicate the virus completely.
You will lose if the virus infect more than 99% of the population of all region.

```
You are the leader, the captain, the commander in chief of this operation.
The world is on your shoulder.
Every decision matters. So be patient.
Every commands you issues will decide the lives of millions.
Act swiftly. The virus waits no one.
Be smart. Plan your strategy.
Let the world see your leadership in action...
```


### Map

```
_________________________________________________________________________
|               ,_   .  ._. _.  .                                       |
|           , _-\','|~\~      ~/      ;-'_   _-'     ,;_;_,    ~~-      |
|  /~~-\_/-'~'--' \~~| ',    ,'      /  / ~|-_\_/~/~      ~~--~~~~'--_  |
|  /              ,/'-/~ '\ ,' _  , '|,'|~                   ._/-, /~   |
|  ~/-'~\_,       '-,| '|. '   ~  ,\ /'~                /    /_  /~     |
|.-~      '|        '',\~|\       _\~ EU  ,_  ,               /|        |
|          '\  NA     /'~        |_/~\\,-,~  \ "        EAs ,_,/ |      |
|           |       /            ._-~'\_ _~| ME           \ ) /         |
|            \   __-\           '/      ~ |\  \_   SAs      /  ~        |
|  .,         '\ |,  ~-_      - |          \\_' ~|  / \ \~ ,            |
|               ~-_'  _;       '\      AF   '-,   \,'  \/  SEA          |
|                '\_,~'\_       \_ _,       /'    '  |, /|'             |
|                  /     \_       ~ |      /         \  ~'; -,_.        |
|                  |       ~\        |    |  ,        '-_, ,; ~ ~\      |
|                   \, SA   /        \    / /|            ,-, ,   -,    |
|                    |    ,/          |  |' |/          ,-   ~ \   '.   |
|                   ,|   ,/           \ ,/              \   OC   |      |
|                   /    |             ~                 -~~-, /   _    |
|                   |  ,-'                                    ~    /    |
|                   / ,'                                      ~         |
|                   ',|  ~                                              |
|                     ~'                                                |
|_______________________________________________________________________|
```

### Specialists

**North America** (You start here)

    Pilot("John"),
    Researcher("James"),
    Doctor("Elsie" ),
    Analyst("Emma"),
    Quarantinist("Kate")
**South America** 

    Researcher("Lopez"),
    Analyst("Silva"),
    Analyst("Roy"),
**Europe**

    Researcher("Henry"),
    Researcher("Sofia"),
**Africa**

    Pilot("Leo"),
    Researcher("Muhammad"),
**Middle East**

    Analyst("Khalid"),
    Quarantinist("Emily")
**!! East Asia !!** (Outbreak)

    Doctor("Ayako"),
    Doctor("Kwang"),
**South Asia**

    Researcher("Aashi"),
    Researcher("Rajat"),
**Southeast Asia**

    Analyst("Hoang"),
    Analyst("Anada"),
    Analyst("Li"),
**Oceania**

    Pilot("Thomas"),
    Researcher("Ella"),
    Analyst("William"),


## Walkthrough

Just type these command in, one by one. Assuming the original settings in the source file
is untouched, you will win the game. This set of commands will walk you through
all possible commands and its functionality. 

__You can always type 'help' or 'manual' to learn more about the game. you can also type 
'restart' to restart the game__

```
help
manual
hire emma
recruit kate // recruit == hire
analyze
hire james
hire john
hire elsie
move europe
hire henry
hire sofia
move africa
hire leo
recruit muhammad
move me // ME is middle east
hire emily
hire khalid
assign analyst
status
move sas // SAs is South Asia 
hire aashi
hire rajat
move sea // SEA is Southeast Asia
hire anada
analyze // analyze == assign analyst
hire li
hire hoang
move east asia
analyze
lockdown // lockdown == assign quarantinist
hire ayako
hire kwang
research // research == assign researcher
assign research
lockdown
research
research
research
treat // treat = assign doctor
status
map
analyze south asia
research
research
treat east asia
assign doctor east asia
assign doctor eas
move south america
hire lopez
hire roy
analyze
hire silva
lockdown east asia
research
research
move east asia
treat
research
research


Congratuation!! You have saved the world from the pandemic !!!
```

### Help
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


