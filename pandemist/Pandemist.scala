package pandemist

import scala.collection.mutable.Buffer
import scala.math._

class Pandemist(startingRegion: Region, private val adventure: Adventure) {
  private var currentRegion = startingRegion
  private var quitCommandGiven = false

  private var pilots = Buffer[Pilot]()
  private var researchers = Buffer[Researcher]()
  private var doctors = Buffer[Doctor]()
  private var analysts = Buffer[Analyst]()
  private var quarantinists = Buffer[Quarantinist]()

  /** This will indicate how much the cure must be developed (in percent) in order
    * for the doctors to treat the patients */
  var cureThresholdForTreatment = 20 // 35.0

  def regions = adventure.regions

  def specialists = pilots ++ researchers ++ doctors ++ analysts ++ quarantinists

  def researchPoints = this.analysts.map(_.researchPointGenerated).sum

  def researchPower = sqrt(this.researchers.map(r => pow(r.researchPower, 2)).sum)

  /** The cure progress meter: measured in percent */
  var curePercentageProgress = 0.0

  /** Determines if the player has indicated a desire to quit the game. */
  def hasQuit = this.quitCommandGiven

  /** Returns the current region of the player. */
  def location = this.currentRegion

  /** Find an availabe specialist, that is, depends on the jobs, find one
    * that are off cooldown or not occupied in a region */
  private def findAvailable(role: String) = role match {
    case "pilot" => this.pilots.find(_.available)
    case "doctor" => this.doctors.find(_.available)
    case "analyst" => this.analysts.find(_.free)
    case "quarantinist" => this.quarantinists.filter(_.free).maxByOption(_.maxCoolDown)
  }

  /** Assign a specialist to a region, that is, use he/she ability
    * on that region */
  private def assign(role: String, region: Region) = this.findAvailable(role) match {
    case Some(specialist) =>
      specialist match {
        case analyst: Analyst => analyst.assignedTo(region)
        case doctor: Doctor => doctor.treating(region)
        case pilot: Pilot => pilot.piloting(region)
        case quarantinist: Quarantinist => quarantinist.assignedTo(region)
      }
    case None =>
      (false, s"You have no available $role...")
  }

  /** Find a region from a string, the string can be the region name or the region code
    * name (ie, NA for north america, SAs for south asia, etc...) */
  private def findRegion(str: String): Option[Region] = {
    if (str.nonEmpty) {
      this.regions.values.find(r => r.name.toLowerCase == str || r.regionCode.toLowerCase == str)
    } else
      Some(this.currentRegion)
  }

  /** Notes: actions such as treat, analyze, move and quarantine can be
    * accompanied by a pilot, allow these action to take place
    * any where on the map. */

  /** Move to a region. If the region is neighboring the current region, you can
    * move there straight away. If the region is not however, a pilot to will move you there,
    * putting the pilot on cooldown. This process is automatic, so make sure you choose the correct region. */
  def move(regionName: String): (Boolean, String) = {
    this.findRegion(regionName) match {
      case Some(destination) =>
        if(destination == this.currentRegion){
          (false, "You can't move to your current location")
        } else if (this.currentRegion.isNeighbor(destination)) {
          this.currentRegion = destination
          (true, s"You succesfully go to ${destination.name}\n")
        } else {
          val (flightSucceed, flightReport) = this.assign("pilot", destination)
          if (flightSucceed) {
            this.currentRegion = destination
            (true, flightReport + s"\nYou fly to ${destination.name}\n")
          } else
            (false, flightReport + s"\nYou fail to fly to ${destination}\n")
        }
      case None =>
        (false, "Your chosen region is invalid.")
    }
  }

  /** Make a overall report on all region or on a specific region. You can only
    * have reports on regions that you have already have analysts assigned to. */
  def report(command: String): (Boolean, String) = {
    val (status, report) =
      if (this.analysts.isEmpty || this.analysts.forall(_.free))
        (false, "Your report is empty, recruit and assign analysts to have information on regions\n")
      else if (command == "all") /** Report all */
        (true, this.analysts.flatMap(_.region).map(_.infectionReport).mkString("\n"))
      else {
        this.findRegion(command) match {
          case Some(region) =>
            if (region.analyst.isDefined)
              (true, region.infectionReport)
            else
              (false, "You have no analyst in that region...")
          case None => (false, "Your chosen region is invalid.")
        }
      }
    (status, "\n" + report)
  }

  /** Recruit a specialist, only on your current location */
  def recruit(name: String): (Boolean, String) = {
    val newRecruit = this.currentRegion.removeSpecialist(name)
    val (status, report) = if (newRecruit.isDefined) {
      newRecruit.get match {
        case pilot: Pilot => this.pilots += pilot
        case doctor: Doctor => this.doctors += doctor
        case researcher: Researcher => this.researchers += researcher
        case analyst: Analyst => this.analysts += analyst
        case quarantinist: Quarantinist => this.quarantinists += quarantinist
      }
      (true, s"Successfully recruit ${newRecruit.get.name}")
    } else
      (false, s"There is no specialist named ${name.toUpperCase} in your current region.\n")
    (status, "\n" + report)
  }

  /** Assign a doctor to treat patients in a region. This will reduce the number of
    * infected patient in that region instantly. */
  def treat(regionName: String): (Boolean, String) = {
    this.findRegion(regionName) match {
      case Some(region) =>
        if (region == this.currentRegion) {
          this.assign("doctor", this.currentRegion)
        } else {
          val header = s"The doctor will be flown to ${region.name}\n"
          val (flightSucceed, flightReport) = this.assign("pilot", region)
          if (flightSucceed) {
            val (treatSucceed, doctorReport) = this.assign("doctor", region)
            (treatSucceed, header + flightReport + doctorReport)
          } else
            (false, header + flightReport)
        }
      case None =>
        (false, "Your chosen region is invalid.\n")
    }
  }

  /** Assign an analyst to collect data on a region. This action is irrevertible, a.k.a, you
    * cannot retrieve the analyst or assign he/she to a new region. Analyzt will slowly
    * produce research points and allow you to monitor the disease in the region through
    * the 'report' command. Research points is *very* important for treating and researching
    * the cure */
  def analyze(regionName: String): (Boolean, String) = {
    this.findRegion(regionName) match {
      case Some(region) =>
        if (region == this.currentRegion) {
          this.assign("analyst", region)
        } else {
          val header = s"\nThe analyst will be flown to ${region.name} to conduct the reporting\n"
          val (flightSucceed, flightReport) = this.assign("pilot", region)
          if (flightSucceed) {
            val (assignSucceed, assignReport) = this.assign("analyst", region)
            (assignSucceed, header + flightReport + assignReport)
          } else {
            (false, header + flightReport)
          }
        }
      case None =>
        (false, "Your chosen region is invalid.")
    }
  }


  /** Assign a quarantinist to lockdown a region. A region under lockdown will have its infection rate
    * reduce to 1, so no new cases during the period. Also, it won't be able to burst and infecting
    * neighboring region. Each quarantinist will have a specific delay power, determining how long
    * he/she can lock down that region. */
  def lockdown(regionName: String): (Boolean, String) = {
    this.findRegion(regionName) match {
      case Some(region) =>
        if (region == this.currentRegion) {
          this.assign("quarantinist", region)
        } else {
          val header = s"\nThe quarantinist will be flown to ${region.name} to lockdown the that region\n"
          val (flightSucceed, flightReport) = this.assign("pilot", region)
          if (flightSucceed) {
            val (lockDownSucceed, lockDownReport) = this.assign("quarantinist", region)
            (lockDownSucceed, header + flightReport + lockDownReport)
          } else {
            (false, header + flightReport)
          }
        }
      case None =>
        (false, "Your chosen region is invalid.")
    }
  }

  /** Conducting research on the cure. */
  def research(): (Boolean, String) = {
    this.curePercentageProgress += this.researchPower
    (true, f"Research successfully, current cure progress: $curePercentageProgress%.2f (+$researchPower%.2f)")
  }

  /** Restart the game */
  def restart(): (Boolean, String) = {
    this.adventure.start()
    (false, "The game has been reset")
  }

  /** Listing out all information about your specialists */
  def specialistStatus: String = {
    if (this.specialists.isEmpty)
      "You have no specialists\n"
    else
      s"You have a total of ${this.specialists.length} specialists:\n\n" + specialists.map(_.description).mkString("\n") + "\n"
  }

  def fullStatus: String = {
    "\n\n" + this.specialistStatus + f"\nCure Progress: ${this.curePercentageProgress}%.2f%% (+$researchPower%.2f)\n" + s"\nResearch Points: ${this.researchPoints}\n"
  }

  /** Signals that the player wants to quit the game. Returns a description of what happened within
    * the game as a result (which is the empty string, in this case). */
  def quit() = {
    this.quitCommandGiven = true
    "You quit"
  }

  /** Returns a brief description of the player's state, for debugging purposes. */
  override def toString = "Now at: " + this.location.name
}
