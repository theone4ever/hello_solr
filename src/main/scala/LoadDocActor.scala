import java.io.File

import akka.actor.Actor
import org.apache.solr.common.SolrInputDocument

import scala.io.Source

case class LoadJsonFromDirReq(filePath: String)
/**
  * An actor to load all json files from a given file directory and convert them into SolrIndexDocument
  */
class LoadDocActor extends Actor{

  val FILE_NAME_FIELD = "fileName"

  val VERSION_FIELD = "version"

  override def receive = {
    case LoadJsonFromDirReq(filePath)=> {
      sender ! loadSolrDocument(filePath)
    }
    case _=>println("Unsupported message")
  }

  def listAllFiles(f: File): List[File] = {
    val all = f.listFiles().toList
    all.filter(_.isFile) ::: all.filter(_.isDirectory).flatMap(dir => listAllFiles(dir))
  }

  def listAllJson(path: String): List[File] = {
    val rootDir = new File(path)
    val allFiles = listAllFiles(rootDir)
    allFiles.filter(f => f.getName.endsWith(".json"))
  }

  def readJsonFile(file: File): String = {
    Source.fromFile(file).getLines.mkString
  }

  import spray.json._

  def loadSolrDocument(path: String): List[SolrInputDocument] = {
    val jsonFiles = listAllJson(path)
    jsonFiles.map(jsonFile => {
      val solrDoc = new SolrInputDocument
      solrDoc.addField(FILE_NAME_FIELD, jsonFile.getAbsolutePath)
      val json = readJsonFile(jsonFile)
      json.parseJson.asJsObject.fields.filter(!_._1.equals(VERSION_FIELD)).foreach {
        case (k: String, v: JsString) => solrDoc.addField(k, v.value)
        case (k: String, v: JsArray) => solrDoc.addField(k, v.elements.toList.map(_.toString()))
        case (k: String, v: JsBoolean) => solrDoc.addField(k, v.value)
        case (k: String, v: JsNumber) => solrDoc.addField(k, v.value)
      }
      solrDoc
    })
  }
}
