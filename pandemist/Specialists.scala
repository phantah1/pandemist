package pandemist

import scala.math._

/** This is the base class for all specialists */
trait Specialist {
  val name: String

  def description: String

  override def toString: String = this.description
}

/** This trait allow for specialists to be assigned to a region */
trait Location {
  var assignedRegion: Option[Region] = None

  def free = assignedRegion.isEmpty

  def occupied = assignedRegion.isDefined

  def region = this.assignedRegion

  def assignedTo(region: Region): (Boolean, String)
}

/** This trait allow for specialists to have cooldown and turns */
trait CoolDown {
  var maxCoolDown: Int
  var coolDown: Int

  def playTurn(): String

  protected def putOnCoolDown() = {
    coolDown = maxCoolDown
  }

  def available = coolDown == 0
}

class Doctor(val name: String,
             val pandemist: Pandemist,
             var maxCoolDown: Int = 3,
             val effectivenessMultiplier: Double = 1
            ) extends Specialist with CoolDown {
  var coolDown = 0

  /** Indicate the percent of the infected populatin the doctor can treat */
  def effectiveness =
    min(this.effectivenessMultiplier * this.pandemist.researchPoints, this.pandemist.curePercentageProgress)

  def treating(region: Region): (Boolean, String) = {
    if (this.pandemist.curePercentageProgress < this.pandemist.cureThresholdForTreatment)
      (false, s"You need to research at least ${this.pandemist.cureThresholdForTreatment}% the cure for the treatment to be effective!!")
    else {
      this.putOnCoolDown()
      val numberOfTreatedPatient = (this.effectiveness * region.infected / 100).toInt
      region.putUnderTreatment(numberOfTreatedPatient)
      (true, f"DOCTOR $name's operation had sucessfully treat $numberOfTreatedPatient(-${100.0 * numberOfTreatedPatient / region.population}%.2f %%) patients !!")
    }
  }

  def playTurn(): String = {
    var report = ""
    if (coolDown == 1) {
      coolDown = 0
      report = s"$name is now off cooldown"
    } else if (coolDown > 1)
      coolDown -= 1
    report
  }

  def description =
    f"DOCTOR $name, Cooldown: $coolDown/$maxCoolDown, Effectiveness: x$effectivenessMultiplier=$effectiveness%.2f"
}


class Researcher(val name: String,
                 val pandemist: Pandemist,
                 val researchmultiplier: Double = 1
                ) extends Specialist {

  /** Indicate how much progress can be made on the cure */
  def researchPower =
    this.researchmultiplier * this.pandemist.researchPoints / 12.0

  def description =
    f"RESEARCHER $name, Research Power: x$researchmultiplier=$researchPower%.2f"
}


class Analyst(val name: String,
              val analyzePower: Int = 1,
              var maxCoolDown: Int = 2,
             ) extends Specialist with CoolDown with Location {
  var coolDown = maxCoolDown
  var researchPointGenerated = 0

  def assignedTo(region: Region): (Boolean, String) = {
    if (region.analyst.isDefined) {
      (false, s"$region already have an analyst.")
    } else {
      this.assignedRegion = Some(region)
      region.analyst = Some(this)
      (true, s"ANALYST $name is now assigned to ${region.name}")
    }
  }


  def playTurn(): String = {
    var report = ""
    if (this.occupied) {
      if (this.coolDown == 1) {
        this.putOnCoolDown()
        this.researchPointGenerated += this.analyzePower
        report = s"ANALYST $name just generate $analyzePower research points !"
      } else if (this.coolDown > 1)
        this.coolDown -= 1
    }
    report
  }

  def description = {
    s"ANALYST $name, Cooldown: $coolDown/$maxCoolDown, Total: $researchPointGenerated RP, AP: $analyzePower, " + (
      if (this.occupied)
        "assigned to " + this.assignedRegion.get.name
      else "")
  }
}


class Pilot(val name: String,
            var maxCoolDown: Int = 3
           ) extends Specialist with CoolDown {
  var coolDown = 0

  def piloting(region: Region): (Boolean, String) = {
    this.putOnCoolDown()
    (true, s"PILOT $name is flying his plane to ${region.name}...\n")
  }

  def playTurn(): String = {
    var report = ""
    if (coolDown == 1) {
      coolDown = 0
      report = s"PILOT $name is now off cooldown"
    } else if (coolDown > 1)
      coolDown -= 1
    report
  }

  def description = s"PILOT $name, Cooldown: $coolDown/$maxCoolDown"
}

class Quarantinist(val name: String,
                   var maxCoolDown: Int = 5,
                  ) extends Specialist with CoolDown with Location {

  var coolDown = maxCoolDown

  def assignedTo(region: Region): (Boolean, String) = {
    if (region.quarantinist.isDefined) {
      (false, s"$region already have quarantinist ${region.quarantinist.get.name} locking it down...")
    } else {
      assignedRegion = Some(region)
      region.quarantinist = Some(this)
      (true, s"QUARANTINIST $name is now assigned to ${region.name}.\n${region.name} will be under lockdown for $maxCoolDown turns !")
    }
  }

  def playTurn(): String = {
    var report = ""
    if (this.occupied) {
      if (this.coolDown == 1) {
        this.putOnCoolDown()
        report =
          s"""
____________________________________________________________________________
|                         !!!!!! ATTENTION !!!!!!!                         |
|              QUARANTINIST $name have to leave ${this.assignedRegion.get.name},
|                  the region is no longer in lockdown                     |
|__________________________________________________________________________|
"""
        this.assignedRegion.get.quarantinist = None
        this.assignedRegion = None
        this.maxCoolDown = max(2, this.maxCoolDown - 1)
      } else if (this.coolDown > 1)
        this.coolDown -= 1
    }
    report
  }

  def description: String = {
    s"QUARANTINIST $name" + (
      if (this.occupied)
        s"assigned to ${this.assignedRegion.get.name}, DURATION LEFT: $coolDown/$maxCoolDown"
      else
        s", max lockdown time: $maxCoolDown"
      )
  }
}
