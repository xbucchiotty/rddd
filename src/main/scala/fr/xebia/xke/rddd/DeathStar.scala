package fr.xebia.xke.rddd

import akka.persistence.PersistentActor

import scala.util.Random

class DeathStar extends PersistentActor {


  override val persistenceId: String = self.path.name


  def receiveCommand = alive(100)

  def alive(healthPoints: Int): Receive = {

    case Torpedo(xwing) if healthPoints > 1 =>
      persist(DeathStartTouched(healthPoints - 1)) { event =>
        if (fireBack) {
          sender() ! FireBack(xwing)
        }
        println(s"Deathstar: ${healthPoints - 1}")

        context become alive(healthPoints - 1)
      }

    case Torpedo(xwing) =>
      persist(DeathStartDestroyed)(_ => {
        context.system.eventStream.publish(DeathStartDestroyed)

        context stop self
      })


  }

  override def receiveRecover: Receive = {
    case DeathStartTouched(remaining) =>
      context become alive(remaining)

    case DeathStartDestroyed =>
      context.system.eventStream.publish(DeathStartDestroyed)
  }


  def fireBack: Boolean =
    Random.nextDouble() < threshold


  val threshold = .04


}

case object DeathStartDestroyed

case class DeathStartTouched(remaining: Int)