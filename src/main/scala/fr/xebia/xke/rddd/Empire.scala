package fr.xebia.xke.rddd

import java.util.UUID

import akka.actor.{Props, Actor}

class Empire(gameId: UUID) extends Actor {

  def receive = PartialFunction.empty

  val almostHiddenDeathStar = context.actorOf(Props[DeathStar], s"deathStar-$gameId")

  override def preStart(): Unit = {
    println("Empire is starting...")
    super.preStart()
  }
}

object Empire {
  def props(gameId: UUID): Props = Props(classOf[Empire], gameId)
}
