package fr.xebia.xke.rddd

import akka.actor.{Actor, ActorPath, Props}

import scala.concurrent.duration._

class Squadron(base: ActorPath, size: Int) extends Actor {

  1 to size map (i => context.actorOf(XWing.props(), s"$squadronId-$i"))





  override def preStart(): Unit = {
    publish(SquadronCreated(squadronId))
  }

  def idle: Receive = {
    case Attack(target) =>
      sendMeIn(1.5.seconds, Arrived)

      publish(SquadronSent(squadronId, target))

      context become traveling(target, nextState = fighting(target))
  }


  def traveling(destination: ActorPath, nextState: Receive): Receive = {
    case Arrived =>
      publish(SquadronArrived(squadronId, destination))

      xwings.foreach(_ ! Attack(destination))

      context become nextState
  }


  def fighting(target: ActorPath): Receive = {
    case FireBack(xwingId) =>

      xwing(xwingId).foreach(context stop)

      publish(XWingLost(squadronId, xwingId))

    case Retreat =>
      sendMeIn(1.5.seconds, Arrived)

      publish(SquadronSent(squadronId, base))

      xwings.foreach(_ ! StopAttack)

      context become traveling(base, nextState = idle)

  }


  def receive = idle



















  def sendMeIn(in: FiniteDuration, message: AnyRef): Unit = {
    import context.dispatcher
    context.system.scheduler.scheduleOnce(in, self, message)
  }

  def publish(event: AnyRef): Unit = {
    context.system.eventStream.publish(event)
  }

  def squadronId = self.path.name

  def xwing(id: XWingId) = context.child(id)

  def xwings = context.children
}

object Squadron {
  def props(base: ActorPath, size: Int): Props =
    Props(classOf[Squadron], base, size)

}