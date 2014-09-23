package fr.xebia.xke.rddd

import akka.actor.{ActorRef, Props, ActorSystem}

object Game extends App {

  val actorSystem = ActorSystem("Universe")

  val empire = actorSystem.actorOf(Props[Empire], "empire")
  val alliance = actorSystem.actorOf(Props[Alliance], "alliance")

  alliance.tell(Attack(empire.path), ActorRef.noSender)

}