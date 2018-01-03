package airwallex.payment.Datasets

import org.json4s._
import org.json4s.jackson.JsonMethods._
import scalaj.http.{Http, HttpResponse}

object SwiftRefEndpoint {

  val EXTERNAL_URL = "http://localhost:10080"
  val SWIFT_REF_PATH = "/api/swift_ref"

  def swiftVerify(swiftCode:String) : Boolean = {
    val response: HttpResponse[String] = Http(EXTERNAL_URL + SWIFT_REF_PATH).param("swift_code", swiftCode).asString
    if(response.body.equals("")) false else parseJsonString(response.body).contains("countryCode")
  }

  def routingVerify(routingValue:String) : Boolean = {
    val response: HttpResponse[String] = Http(EXTERNAL_URL + SWIFT_REF_PATH).param("routing_value", routingValue).asString
    if(response.body.equals("")) false else parseJsonString(response.body).contains("countryCode")
  }

  def swiftRoutingVerify(swiftCode:String, routingValue:String) : Boolean = {
    val response: HttpResponse[String] = Http(EXTERNAL_URL + SWIFT_REF_PATH).params(Seq("swift_code" -> swiftCode, "routing_value" -> routingValue)).timeout(connTimeoutMs = 5000, readTimeoutMs = 10000).asString
    if(response.body.equals("")) false else parseJsonString(response.body).contains("countryCode")
  }

  def parseJsonString(jsonStr:String): Map[String, Any] = {
    implicit val formats = org.json4s.DefaultFormats
    parse(jsonStr).extract[Map[String,Any]]

  }

  def safeString: String => String = s => if (s == null) "" else s

  def main(args: Array[String]): Unit = {

    printf("swift test works %s \n" , swiftVerify("ABKKAEAD"))
    printf("routing test works %s \n" , routingVerify("053112479"))
    printf("swift_routing test works %s \n" , swiftRoutingVerify("BOFAUS3N", "026009593"))

  }

}
