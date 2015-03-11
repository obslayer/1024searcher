import akka.io._
import akka.actor.{Props, Actor, ActorSystem}
import akka.event.Logging
import java.net.InetSocketAddress
import akka.pattern.{ask, pipe}
import java.io.File
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
class ReqParser extends Actor{
	implicit val timeout = Timeout(20 seconds)
	def receive = {
		case a:String =>{
			val args = a.split(" ")
			val mode=args(0)

			if(Set("search","download").foldLeft(false)((a,b) => a|(b==mode))){
				println(s"received from server ${a}")
				var from =1
				var to =1
				if (args.indexOf("--from") != -1)
				from = args(args.indexOf("--from")+1).trim.toInt
				if (args.indexOf("--to") != -1)
				to = args(args.indexOf("--to")+1).trim.toInt
				//val searcher = context.actorOf(Props[HttpSearcher],"searcher")
				val query = args.last
				val x = from to to
				x.par.foreach({in=>
					val searcher = context.actorOf(Props[HttpSearcher],s"searcher${in}")
					searcher!(SearchPara(mode, in,query))
				})
u		}
			else{
				sender()!(s"${a.trim} does not contain a valid command\n")
			}
			
		}
		case _ =>{
			println{"unparsable contents received @ ReqParser"}
		}

	}
}