import akka.io._
import akka.actor.{Props, Actor, ActorSystem}
import akka.event.Logging
import java.net.InetSocketAddress
import java.io.File
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import akka.io.{IO, Tcp}
import org.jsoup._;
import Tcp._
import akka.util.ByteString
import org.jsoup.nodes.Document;
import scala.collection.JavaConverters._;
class HttpSearcher extends Actor{
	def receive ={
		case SearchPara(mode, page, keyword)=>{
			try{
				val fromURL= true
				val useProxy = false
				var doc:Document= new Document("")
				if (!fromURL){doc = Jsoup.parse(new File("/home/wang/workplace/1024searcher/page.html"), "UTF-8")}
				if(fromURL){
					val url:URL = new URL(s"http://c1521.biz.tm/thread0806.php?fid=2&search=&page=${page}")
					println(s"searching http://c1521.biz.tm/thread0806.php?fid=2&search=&page=${page}")
					val add:InetSocketAddress = new InetSocketAddress("127.0.0.1", 1080)
					val proxy:Proxy = new Proxy(Proxy.Type.SOCKS, add)
					var conn:URLConnection = url.openConnection()
					if (useProxy) conn = url.openConnection(proxy)
					val input:InputStream = conn.getInputStream()
					val rder:InputStreamReader = new InputStreamReader(input, "GBK")
					val rd:BufferedReader = new BufferedReader(rder)
					var buf = ""
					var content = ""
					while (!rd.ready){Thread sleep 3000}
					while (buf!=null){
						buf=rd.readLine()
						content=content+buf
					}
					doc = Jsoup.parse(content)
					//print(doc)
				}
				val tokenlist=doc.body.getElementsByTag("a").iterator.asScala.toList.map(_.toString).filter(in => in.contains("htm_data") && (!in.contains("title"))).map(_.split("\""))
				val bigmap = tokenlist.map(_(1)).zip(tokenlist.map(_(6))).toMap.map(_.swap)
				val keylist = bigmap.keys.filter(_.contains(keyword.trim))
				//println(keylist.last)
				//sender!(keylist.map(bigmap(_)).foldLeft("")((a,b) => a+("http://c1521.biz.tm/"+b+'\n')))
				val res = keylist.map(bigmap(_)).foldLeft("")((a,b) => a+("http://c1521.biz.tm/"+b+'\n'))
				context.actorSelection("akka://1024/system/IO-TCP/selectors/$a/1")!Write(ByteString(res))
				println(res)
//				println("FROMHTTPSEARCHER")
//				println(keylist.map(bigmap(_)).toString)
			}
			catch{
				case e:IOException => println(e)
			}
		}
	}
}
