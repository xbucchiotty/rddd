package fr.xebia.xke.rddd

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem, Props}

object StartNewGame extends App {

  Game.start(UUID.randomUUID())

}

object ResumeGame extends App {

  Game.start(UUID.fromString(args(0)))

}

object Game {
  def start(gameId: UUID) {
    val actorSystem = ActorSystem("Universe")

    val empire = actorSystem.actorOf(Empire.props(gameId), "empire")
    val alliance = actorSystem.actorOf(Alliance.props(gameId), "alliance")

    alliance.tell(Attack(empire.path), ActorRef.noSender)
  }
}