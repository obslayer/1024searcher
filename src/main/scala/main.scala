import akka.io._
import akka.actor.{Props, Actor, ActorSystem}
import akka.event.Logging
import java.net.InetSocketAddress
import akka.pattern.{ask, pipe}
import java.io.File
import akka.util.Timeout
import scala.concurrent.duration._
object _1024searcher {
	implicit val timeout = Timeout(20 seconds)
	def main (args:Array[String])={
		val system =  ActorSystem("1024")
		val server = system.actorOf(Props[Server],"tcpserver")
	}

}