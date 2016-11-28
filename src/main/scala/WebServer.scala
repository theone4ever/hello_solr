import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import org.apache.solr.common.SolrDocument
import spray.json.DefaultJsonProtocol

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._



/**
  * Created by eqqiwng on 11/28/16.
  */


case class Document(fileName: String,
                    name: String,
                    `type`: String)


object WebServer {


  // formats for unmarshalling and marshalling
//    implicit val orderM: ToResponseMarshaller[List[SolrDocument]] = ???
  implicit val documentFormat = jsonFormat3(Document)

  // (fake) async database query api

  def main(args: Array[String]) {

    // needed to run the route
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val route: Route = path("search") {
      get {
        parameter("query".as[String]) { query =>
          val future: Future[List[SolrDocument]] = search(query)
          onSuccess(future) {
            case list: List[SolrDocument] => {
              val docList: List[Document] = list.map { doc => Document(
                doc.getFieldValue("fileName").toString,
                doc.getFieldValue("name").toString,
                doc.getFieldValue("type").toString)
              }
              complete(docList)
            }
          }

        }
      }
    }


    val bindingFuture = Http().bindAndHandle(route, "localhost", 8001)
  }

  def search(query: String): Future[List[SolrDocument]] = {
    // needed to run the route
    implicit val system = ActorSystem()
    implicit val timeout = Timeout(15 seconds)

    //    val system = ActorSystem("System")
    val solrSearchActor = system.actorOf(Props(new SolrSearchActor()))
    val future = solrSearchActor ? SearchRequest(query)
    Await.result(future, 5 second)
    future.mapTo[List[SolrDocument]]
  }
}
