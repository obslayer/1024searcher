import akka.actor.{Actor, ActorRef, Props}
import akka.io.{IO, Tcp}
import akka.util.ByteString
import java.net.InetSocketAddress
import akka.actor._
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import scala.concurrent.duration._
import Tcp._
import scala.concurrent.Await
class Server extends Actor{
	import context.system
	import Tcp._
	IO(Tcp)!Bind(self, new InetSocketAddress("localhost",0))
	def receive = {
		case b @ Bound(localAddress)=>{
			println(localAddress)
		}
		case CommandFailed(_:Bind) => context stop self
		case c @ Connected(remote, local)=>
		val handler = context.actorOf(Props[TcpHandler],"handler")
		val connection = sender()
		connection ! Register(handler)
	}
}
class TcpHandler extends Actor {
	import Tcp._
	implicit val timeout = Timeout(20 seconds)
	def receive = {
		case Received(data) =>{
			val reqparser =	context.actorOf(Props[ReqParser])
			reqparser!(data.utf8String)
			println(sender.path.toString)
		}			
		case str:String =>{
			context.actorSelection("akka://1024/system/IO-TCP/selectors/$a/1")!Write(ByteString(str+'\n'))
		}
		case PeerClosed     => context stop self
	}
}
