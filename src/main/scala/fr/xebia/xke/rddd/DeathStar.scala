package fr.xebia.xke.rddd

import akka.actor.Actor

import scala.util.Random

class DeathStar extends Actor {

  def receive = alive(100)


  def alive(healthPoints: Int): Receive = {


    case Torpedo(xwing) if healthPoints > 1 =>
      if (fireBack) {
        sender() ! FireBack(xwing)
      }

      context become alive(healthPoints - 1)



    case Torpedo(xwing) =>
      context stop self


  }


  override def postStop(): Unit = {
    println("End of DeathStar")
  }

  def fireBack: Boolean =
    Random.nextDouble() < threshold


  val threshold = .04
}