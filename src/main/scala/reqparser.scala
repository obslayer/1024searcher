import akka.io._
import akka.actor.{Props, Actor, ActorSystem}
import akka.event.Logging
import java.net.InetSocketAddress
import akka.pattern.{ask, pipe}
import java.io.File
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Await
class ReqParser extends Actor{
	implicit val timeout = Timeout(20 seconds)
	def receive = {
		case a:String =>{
			val args = a.split(" ")
			val mode=args(0)
			if(Set("search","download").foldLeft(false)((a,b) => a|(b==mode))){
				var from =1
				var to =1
				if (args.indexOf("--from") != -1)
				from = args(args.indexOf("--from")+1).trim.toInt
				if (args.indexOf("--to") != -1)
				to = args(args.indexOf("--to")+1).trim.toInt
				val searcher = context.actorOf(Props[HttpSearcher],"searcher")
				val query = args.last
				val rez = (from to to).map(SearchPara(mode,_,query)).par.map(in => Await.result(searcher?(in), 20 seconds).asInstanceOf[String]).reduce(_+_)
				sender()!rez
			}
			else{
				sender()!(s"${a.trim} does not contain a valid command\n")
			}
			
		}
		case _ =>{
			println{"unparsable contents received @ ReqParser"}
		}

	}
}