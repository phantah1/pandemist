package pandemist

import scala.collection.mutable.Map
import scala.math._
import scala.util.Random


class Region(val name: String,
             val regionCode: String,
             var description: String,
             val population: Int,
             var baseInfectionRate: Double = 1.5) {
  private val neighbors = Map[String, Region]()
  private var specialists = Map[String, Specialist]()
  private var infectedInDouble: Double = 0.0
  private var burstThreshold = 1
  private var underTreatment = 0

  /** Assigned specialists */
  var analyst: Option[Analyst] = None
  var quarantinist: Option[Quarantinist] = None

  def underLockDown = this.quarantinist.isDefined

  def putUnderTreatment(number: Int) = {
    this.underTreatment = number
  }

  /** Infection */
  private def setInfected(number: Int) = {
    this.infectedInDouble = number.toDouble
  }

  def infectionRate =
    if(this.underLockDown)
      1.001
    else
      this.baseInfectionRate

  def infected = this.infectedInDouble.toInt

  def percentInfected = 100.0 * this.infectedInDouble / this.population

  def burstChance =
    if(this.underLockDown)
      0.0
    else
      this.percentInfected / 2

  def update(): String = {
    if(this.underTreatment > 0) {
      this.infectedInDouble -= this.underTreatment
      /** The treated patient can still be reinfected but will have some resistance to the disease,
        * reducing somewhat the infection rate of the disease in the population */
      this.baseInfectionRate = pow(this.baseInfectionRate, 1.0-this.underTreatment.toDouble / this.population)
      this.underTreatment = 0
    } else {
      this.infectedInDouble = min(this.infectedInDouble*this.infectionRate, this.population)
    }
    if(this.percentInfected > this.burstThreshold && Random.nextDouble() < this.burstChance / 100.0 ) {
      this.burst()

      s"""
____________________________________________________________________________
|                         !!!!!! WARNING !!!!!!!                           |
          $name just burst, infecting it's neighborring region
|__________________________________________________________________________|
"""
    } else ""
  }

  private def burst() = {
    this.neighbors.foreach(r => r._2.setInfected(r._2.infected + 300))
  }

  /** Helper commands */
  def setSpecialist(list: Vector[Specialist]) = {
    list.foreach(s => this.specialists += s.name.toLowerCase -> s)
  }

  def removeSpecialist(specialistName: String): Option[Specialist] =
    this.specialists.remove(specialistName)

  def setNeighbors(neighbor: Vector[(String, Region)]) = {
    this.neighbors ++= neighbor
  }

  def setInfected(number: Double) = {
    this.infectedInDouble = number
  }

  def isNeighbor(region: String): Boolean = this.neighbors.exists(_._1 == region)

  def isNeighbor(region: Region): Boolean = this.neighbors.exists(_._2 == region)

  /** Reporting */

  def isInfected =
    if (this.analyst.isDefined)
      this.infected > 0
    else false

  def isDoomed =
    if (this.analyst.isDefined)
      this.percentInfected > 99.0
    else false

  def infectionReport =
    if (this.analyst.isDefined)
      f"$name: Infected: $infected/$population ($percentInfected%2.2f%%), R_0: $infectionRate%.2f\n"
    else
      "\n"

  def neighborsReport =
    "Neighboring regions: " + this.neighbors.map(p => p._1 + s" (${p._2.regionCode})").mkString(" || ").toUpperCase + "\n"

  def specialistReport =
    if (this.specialists.nonEmpty)
      "Specialists available here: \n\n" + this.specialists.map(_._2.description).mkString("\n") + "\n"
    else
      "\n"

  def fullDescription =
    this.description + (if(this.underLockDown) "[LOCKDOWN]\n" else "\n") + this.neighborsReport + "\n" + this.specialistReport + "\n" + this.infectionReport

  override def toString =
    this.name + ": " + this.description.replaceAll("\n", " ").take(150)
}
