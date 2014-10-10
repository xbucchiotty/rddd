package fr.xebia.xke.rddd

import akka.actor.{ActorPath, Props}
import akka.persistence.{RecoveryCompleted, PersistentActor}

import scala.concurrent.duration._

class Squadron(base: ActorPath, size: Int) extends PersistentActor {

  val squadronId = self.path.name

  override val persistenceId: String = squadronId


  1 to size map (i => context.actorOf(XWing.props(), s"$squadronId-$i"))


  override def preStart(): Unit = {
    publish(SquadronCreated(persistenceId))
    super.preStart()
  }

  def idle: Receive = {
    case Attack(target) =>
      persist(SquadronSentToFight(squadronId, target)) { event =>
        publish(event)

        travel()

        context become traveling(prepareToFight(target))
      }
  }


  def travel() {
    sendMeIn(1.5.seconds, Arrived)
  }

  def prepareToFight(target: ActorPath): () => Unit = {
    () =>
      persist(SquadronArrivedToFight(squadronId, target)) {
        event => publish(event)

          attack(target)

          context become fighting(target)
      }
  }

  def attack(target: ActorPath) {
    xwings.foreach(_ ! Attack(target))
  }

  def prepareToRetreat(target: ActorPath): () => Unit = {
    () =>
      persist(SquadronArrivedToRetreat(squadronId, target)) {
        event => publish(event)

          context become idle
      }
  }

  def traveling(transition: () => Unit): Receive = {
    case Arrived =>
      transition()

  }


  def fighting(target: ActorPath): Receive = {
    case FireBack(xwingId) =>

      persist(XWingLost(squadronId, xwingId)) { event =>
        publish(event)

        xwing(xwingId).foreach(context stop)
      }

    case Retreat =>

      persist(SquadronSentToRetreat(squadronId, base)) { event =>
        sendMeIn(1.5.seconds, Arrived)

        xwings.foreach(_ ! StopAttack)

        context become traveling(prepareToRetreat(base))
      }

  }


  def receiveCommand = idle


  private var lastTransitionToRecover: Option[() => Unit] = None

  def receiveRecover: Receive = {
    case event@SquadronSentToFight(`squadronId`, target) =>
      publish(event)
      context become traveling(prepareToFight(ActorPath.fromString(target)))
      lastTransitionToRecover = Some(() => travel())


    case event@SquadronSentToRetreat(`squadronId`, target) =>
      publish(event)
      context become traveling(prepareToRetreat(ActorPath.fromString(target)))
      lastTransitionToRecover = Some(() => travel())


    case event@SquadronArrivedToFight(`squadronId`, target) =>
      publish(event)
      context become fighting(ActorPath.fromString(target))
      lastTransitionToRecover = Some(() => attack(ActorPath.fromString(target)))


    case event@SquadronArrivedToRetreat(`squadronId`, _) =>
      publish(event)
      context become idle

    case event@XWingLost(`squadronId`, xwingId) =>
      publish(event)
      xwing(xwingId).map(context stop)

    case RecoveryCompleted =>
      lastTransitionToRecover.map(_.apply())
  }


  def sendMeIn(in: FiniteDuration, message: AnyRef): Unit = {
    import context.dispatcher
    context.system.scheduler.scheduleOnce(in, self, message)
  }

  def publish(event: AnyRef): Unit = {
    context.system.eventStream.publish(event)
  }

  def xwing(id: XWingId) = context.child(id)

  def xwings = context.children
}

object Squadron {
  def props(base: ActorPath, size: Int): Props =
    Props(classOf[Squadron], base, size)

}