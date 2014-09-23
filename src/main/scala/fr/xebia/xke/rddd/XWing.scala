package fr.xebia.xke.rddd

import akka.actor.{Cancellable, Actor, Props}

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._

class XWing extends Actor {


  def receive = {
    case Attack(targetPath) =>

      val aggregate = sender()
      val target = context.actorSelection(targetPath)

      repeatEvery(113.milliseconds) {
        target.tell(Torpedo(from = xwingId), sender = aggregate)
}

    case StopAttack =>
      operation.foreach(_.cancel())
      operation = None
  }

  var operation = Option.empty[Cancellable]

  override def postStop(): Unit = {
    operation.foreach(_.cancel())
  }

  def xwingId = self.path.name

  def repeatEvery(every: FiniteDuration)(f: => Unit) {
    import context.dispatcher
    operation = Some(context.system.scheduler.schedule(every, every)(f))
  }
}

object XWing {
  def props(): Props = Props[XWing]
}