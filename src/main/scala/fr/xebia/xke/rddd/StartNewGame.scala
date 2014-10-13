package fr.xebia.xke.rddd

import java.util.UUID

import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, MemberUp}
import akka.remote.RemoteScope
import com.typesafe.config.ConfigFactory

object StartNewGame extends App {

  Game.start(UUID.randomUUID())

}

object ResumeGame extends App {

  Game.start(UUID.fromString(args(0)))

}

object Game {
  def start(gameId: UUID) {
    val actorSystem = ActorSystem("Universe", ConfigFactory.load("game.conf"))

    val cluster = Cluster(actorSystem)

    cluster.subscribe(actorSystem.actorOf(ClusterListener.props(gameId), "clusterListener"), classOf[MemberEvent])

  }
}

class ClusterListener(gameId: UUID) extends Actor {

  private var allianceNode = Option.empty[ActorRef]
  private var empireNode = Option.empty[ActorRef]

  def receive: Receive = {
    case MemberUp(member) if member.roles.contains("empire") =>
      println(s"Empire is connected with address [${member.address}]")
      empireNode = Some {
        context.system.actorOf(Empire.props(gameId).withDeploy(Deploy(scope = RemoteScope(member.address))), "empire")
      }
      startIfPossible()

    case MemberUp(member) if member.roles.contains("alliance") =>
      println(s"Alliance is connected with address [${member.address}]")
      allianceNode = Some {
        context.system.actorOf(Alliance.props(gameId).withDeploy(Deploy(scope = RemoteScope(member.address))), "alliance")
      }
      startIfPossible()
  }

  def startIfPossible(): Unit = {
    (allianceNode, empireNode) match {
      case (Some(alliance), Some(empire)) =>
        println(s"${alliance.path} attacks ${empire.path}")
        alliance.tell(Attack(empire.path), ActorRef.noSender)

      case _ =>
    }
  }
}

object ClusterListener {
  def props(gameId: UUID): Props = Props(classOf[ClusterListener], gameId)
}

object StartAlliance extends App {
  val actorSystem = ActorSystem("Universe", ConfigFactory.load("alliance.conf"))
}

object StartEmpire extends App {
  val actorSystem = ActorSystem("Universe", ConfigFactory.load("empire.conf"))
}