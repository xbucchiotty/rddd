package fr.xebia.xke.rddd

import akka.actor.{Props, Actor}

class Empire extends Actor {

  def receive = PartialFunction.empty

  val almostHiddenDeathStar = context.actorOf(Props[DeathStar], "deathStar-1")
}
