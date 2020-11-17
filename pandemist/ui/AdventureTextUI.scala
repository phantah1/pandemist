package pandemist.ui

import pandemist.Adventure
import scala.io.StdIn._

/** The singleton object `AdventureTextUI` represents a fully text-based version of the
  * Adventure game application. The object serves as a possible entry point for the game,
  * and can be run to start up a user interface that operates in the text console.
  * @see [[AdventureGUI]] */
object AdventureTextUI extends App {

  private val game = new Adventure
  def player = game.player
  this.run()


  /** Runs the game. First, a welcome message is printed, then the player gets the chance to
    * play any number of turns until the game is over, and finally a goodbye message is printed. */
  private def run() = {
    println(this.game.welcomeMessage)
    while (!this.game.isOver) {
      this.printRegionInfo()
      this.playTurn()
    }
    println("\n" + this.game.goodbyeMessage)
  }


  /** Prints out a description of the player character's current location, as seen by the character. */
  private def printRegionInfo() = {
    val region = this.player.location
    println("\n" + region.name)
    println("-" * region.name.length)
    println(region.fullDescription + "\n")
  }


  /** Requests a command from the player, plays a game turn accordingly, and prints out a report of what happened.  */
  private def playTurn() = {
    val command = readLine("Command: ")
    val turnReport = this.game.playTurn(command)
    if (!turnReport.isEmpty) {
      println(turnReport)
    }
  }
}


