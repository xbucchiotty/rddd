package fr.xebia.xke.rddd

import java.util.UUID

import akka.actor.{Props, ActorRef, Actor}
import fr.xebia.xke.rddd.infra.Base

class Alliance(gameId : UUID) extends Actor {

  val hiddenBase = context.actorOf(Base.props(), "Yavin4")


  val squandronNames = List("red")


  val squadrons = squandronNames.map(name => context.actorOf(Squadron.props(hiddenBase.path, 4), s"$name-$gameId"))




  var squadronsHealth = squadrons.map(squadron => (squadron.path.name, 4)).toMap

  override def preStart(): Unit = {
    context.system.eventStream.subscribe(self, classOf[XWingLost])
    context.system.eventStream.subscribe(self, DeathStartDestroyed.getClass)
    val gameListener = context.actorOf(Props[SquadronEventListener], "squadronEventListener")

    context.system.eventStream.subscribe(gameListener, classOf[SquadronEvent])
    context.system.eventStream.subscribe(gameListener, DeathStartDestroyed.getClass)
  }

  def receive = {
    case Attack(empire) =>
      val deathStar = empire / "deathStar-*"
      squadrons.foreach(squadron => squadron ! Attack(deathStar))

    case XWingLost(squadronId, _) =>
      val previousHealth = squadronsHealth(squadronId)

      if (previousHealth == 1) {
        squadron(squadronId).map(_ ! Retreat)
      } else {
        squadronsHealth = squadronsHealth.updated(squadronId, previousHealth - 1)
      }

    case DeathStartDestroyed =>
      squadrons.foreach(_ ! Retreat)
  }










  def squadron(id: SquadronId): Option[ActorRef] =
    context.child(id)
}

object Alliance {
  def props(gameId: UUID): Props = Props(classOf[Alliance], gameId)
}


class SquadronEventListener extends Actor {

  def receive: Receive = {
    case SquadronCreated(squadron) =>
      println(s"<$squadron> is created")

    case SquadronSentToRetreat(squadron, destination) =>
      println(s"<$squadron> is traveling to $destination to retreat")

    case SquadronSentToFight(squadron, destination) =>
      println(s"<$squadron> is traveling to $destination to fight")

    case SquadronArrivedToFight(squadron, destination) =>
      println(s"<$squadron> arrives at $destination to fight")

    case SquadronArrivedToRetreat(squadron, destination) =>
      println(s"<$squadron> arrives at $destination to retreat")

    case XWingLost(squadron, xwing) =>
      println(s"<$squadron> lost $xwing")

    case DeathStartDestroyed =>
      println("We won")
  }
}