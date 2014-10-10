package fr.xebia.xke.rddd

import akka.actor.ActorPath

/////////
//Commands
/////////
case class Attack(target: ActorPath)

case object Arrived

case class FireBack(xwing: XWingId)

case object Retreat


/////////
//Actions
/////////
case class Torpedo(from: XWingId)
case object StopAttack


////////
//Events
////////
trait SquadronEvent
case class SquadronCreated(squadron: SquadronId) extends SquadronEvent

case class SquadronSentToFight(squadron: SquadronId,
                        location: Location) extends SquadronEvent

case class SquadronSentToRetreat(squadron: SquadronId,
                        location: Location) extends SquadronEvent

case class SquadronArrivedToFight(squadron: SquadronId,
                           location: Location) extends SquadronEvent

case class SquadronArrivedToRetreat(squadron: SquadronId,
                           location: Location) extends SquadronEvent

case class XWingLost(squadron: SquadronId,
                     xwing: XWingId) extends SquadronEvent