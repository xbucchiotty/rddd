package fr.xebia.xke.rddd

import java.util.UUID

import akka.actor.{Props, Actor}

class Empire(gameId: UUID) extends Actor {

  def receive = PartialFunction.empty

  val almostHiddenDeathStar = context.actorOf(Props[DeathStar], s"deathStar-$gameId")
}

object Empire {
  def props(gameId: UUID): Props = Props(classOf[Empire], gameId)
}
