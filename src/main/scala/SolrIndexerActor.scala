import akka.actor.{Actor, Props}
import org.apache.solr.client.solrj.impl.HttpSolrClient
import org.apache.solr.common.SolrInputDocument

case class IndexDocsMsg(docs: List[SolrInputDocument])

/**
  * Created by eqqiwng on 11/24/16.
  */
class SolrIndexerActor extends Actor{

  val FILE_NAME_FIELD = "fileName"

  override def receive = {
    case IndexDocsMsg(docs: List[SolrInputDocument])=>{
      val client = new HttpSolrClient("http://localhost:8983/solr/mycore")
      docs.foreach(doc=>{
        println(s"SolrDoc: ${doc.getField(FILE_NAME_FIELD)}")
        client.add(doc)
      })
      client.commit(false, false, true)
      sender ! true
    }
    case _=> println("Unsupported request")
  }
}
