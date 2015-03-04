name := "My Project"

version := "1.0"

scalaVersion := "2.10.3"

scalacOptions +="-target:jvm-1.7"

resolvers ++= Seq(
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    Resolver.sonatypeRepo("Public")
)

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.3.6",
    "net.debasishg" %% "redisclient" % "2.13",
    "org.jsoup" % "jsoup" % "1.8.1",
    "com.github.scopt" %% "scopt" % "3.3.0"
)

initialCommands in console := """
import java.util.concurrent.TimeoutException;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.pattern.{ask, pipe}
import akka.actor._;
import akka.japi.Function;
import akka.pattern.Patterns;
import akka.util.Timeout;
import java.io.File;
"""

