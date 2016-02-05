package fr.xebia.xke.rddd

import akka.actor._

import scala.concurrent.duration._

class Squadron(base: ActorPath, size: Int) extends Actor with ActorLogging {

  1 to size map (i => context.actorOf(XWing.props(), s"$squadronId-$i"))






  //TODO#2 : Receive Attack   => sendMe Arrived + SquadronSent
  //TODO#3 : Receive Arrived  => Attack -> XWing + SquadronArrived
  //TODO#4 : Receive FireBack => stop XWing + XWingLost
  //TODO#6 : Retreat          => StopAttack + sendMe Arrived + SquadronSent
  //TODO#7 : SquadronCreated  =>
  def receive = PartialFunction.empty



















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