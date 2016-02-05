name := "Reactive Domain Driven Design"

scalaVersion := "2.11.7"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.1" withSources()

libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.4.1" withSources()

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"


fork in run := true

cancelable in Global := true