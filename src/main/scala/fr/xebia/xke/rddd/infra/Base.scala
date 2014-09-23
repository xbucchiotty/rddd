package fr.xebia.xke.rddd.infra

import akka.actor.{Props, Actor}

class Base extends Actor {

  def receive = PartialFunction.empty
}

object Base {
  def props(): Props = Props[Base]
}