/**
  * Created by eqqiwng on 11/23/16.
  */

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import org.apache.solr.common.SolrInputDocument

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Main {


  val filePath = "/Users/eqqiwng/project/taaas-production/data/tools"
  val system = ActorSystem("System")

  def main(args: Array[String]): Unit = {


    val loadDocActor = system.actorOf(Props(new LoadDocActor()))
    val solrIndexerActor = system.actorOf(Props(new SolrIndexerActor()))
    implicit val timeout = Timeout(25 seconds)
    val future = loadDocActor ? LoadJsonFromDirReq(filePath)

    val result = future.mapTo[List[SolrInputDocument]].flatMap {
      docs => solrIndexerActor ? IndexDocsMsg(docs)
    }

    Await.result(result, 1 second)
    println(result)

    system.shutdown

  }
}