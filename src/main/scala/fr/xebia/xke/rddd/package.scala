package fr.xebia.xke

import akka.actor.ActorPath

package object rddd {

  type SquadronId = String

  type XWingId = String

  type Location = String

  implicit def serializablePathOfActorRef(path: ActorPath): String =
    path.toSerializationFormat
}
