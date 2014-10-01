package fr.xebia.xke.rddd

import akka.actor.{Props, ActorRef, Actor}
import fr.xebia.xke.rddd.infra.Base

class Alliance extends Actor {

  val hiddenBase = context.actorOf(Base.props(), "Yavin4")

  val squadrons = List(context.actorOf(Squadron.props(hiddenBase.path, 4), "red"))




  var squadronsHealth = squadrons.map(squadron => (squadron.path.name, 4)).toMap

  override def preStart(): Unit = {
    context.system.eventStream.subscribe(self, classOf[XWingLost])
    val gameListener = context.actorOf(Props[SquadronEventListener], "squadronEventListener")

    context.system.eventStream.subscribe(gameListener, classOf[SquadronEvent])
  }

  def receive = {
    case Attack(empire) =>
      val deathStar = empire / "deathStar-1"
      squadrons.foreach(squadron => squadron ! Attack(deathStar))

    case XWingLost(squadronId, _) =>
      val previousHealth = squadronsHealth(squadronId)

      if (previousHealth == 1) {
        squadron(squadronId).map(_ ! Retreat)
      } else {
        squadronsHealth = squadronsHealth.updated(squadronId, previousHealth - 1)
      }
  }










  def squadron(id: SquadronId): Option[ActorRef] =
    context.child(id)
}

class SquadronEventListener extends Actor {

  def receive: Receive = {
    case SquadronCreated(squadron) =>
      println(s"<$squadron> is created")

    case SquadronSent(squadron, destination) =>
      println(s"<$squadron> is traveling to $destination")

    case SquadronArrived(squadron, destination) =>
      println(s"<$squadron> arrives at $destination")
  }
}