package fr.xebia.xke.rddd

import akka.actor.{ActorRef, Actor}
import fr.xebia.xke.rddd.infra.Base

class Alliance extends Actor {

  val hiddenBase = context.actorOf(Base.props(), "Yavin4")

  val squadrons = List(context.actorOf(Squadron.props(hiddenBase.path, 4), "red"))






  //TODO#1 : Find death star and order Attack
  //TODO#5 : Watch XWing lost
  //TODO#8 : Watch all SquadronEvent
  def receive = PartialFunction.empty











  def squadron(id: SquadronId): Option[ActorRef] =
    context.child(id)
}