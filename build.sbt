name := "Reactive Domain Driven Design"

libraryDependencies += "com.typesafe.akka" %% "akka-persistence-experimental" % "2.3.6"

libraryDependencies += "com.typesafe.akka" %% "akka-http-core-experimental" % "0.7"

libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % "2.3.6"

libraryDependencies += "com.typesafe.akka" %% "akka-stream-experimental" % "0.7"

resolvers += "krasserm at bintray" at "http://dl.bintray.com/krasserm/maven"

libraryDependencies += "com.github.krasserm" %% "akka-persistence-kafka" % "0.3.2"