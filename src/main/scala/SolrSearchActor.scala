import akka.actor.Actor
import akka.util.Timeout
import io.ino.solrs.AsyncSolrClient
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.impl.XMLResponseParser
import org.apache.solr.client.solrj.response.QueryResponse
import org.asynchttpclient.DefaultAsyncHttpClient
import io.ino.solrs.future.ScalaFutureFactory.Implicit

import scala.concurrent.Future
import scala.collection.JavaConversions._




/**
  * Created by eqqiwng on 11/28/16.
  */


case class SearchRequest(query: String)
class SolrSearchActor extends Actor {
  override def receive = {
    case SearchRequest(query)=>{
      import scala.concurrent.ExecutionContext.Implicits.global


      val solr = AsyncSolrClient.Builder("http://localhost:8983/solr/mycore")
            .withHttpClient(new DefaultAsyncHttpClient())
            .withResponseParser(new XMLResponseParser())
            .build

          val response: Future[QueryResponse] = solr.query(new SolrQuery(query))
          response.onSuccess {
            case qr => {
              println(s"found ${qr.getResults.getNumFound} docs")
              sender ! qr.getResults.toList
              solr.shutdown()
            }
          }
    }

  }
}
