import sbt.Keys._

name := "SolrApp"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "io.ino" %% "solrs" % "2.0.0-RC1"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

//libraryDependencies += "io.spray" %%  "spray-json" % "1.3.2"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.0"

libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.0"

//libraryDependencies += "com.typesafe.akka" %% "akka-http-core" % "2.4.7"

//libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % "2.4.7"

//libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.4"

mainClass in (Compile, run) := Some("WebServer")