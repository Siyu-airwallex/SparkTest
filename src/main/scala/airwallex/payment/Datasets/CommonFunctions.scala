package airwallex.payment.Datasets


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scalaj.http.{Http, HttpResponse}

object CommonFunctions {

  val EXTERNAL_URL = "http://localhost:10080"

  def swiftVerify(swiftCode:String) : Boolean = {
    val response: HttpResponse[String] = Http(EXTERNAL_URL + "/api/swift_ref/swift").param("swift_code", swiftCode).asString
    if(response.body.equals("")) false else parseJsonString(response.body).contains("countryCode")

  }

  def routingVerify(routingValue:String) : Boolean = {
    val response: HttpResponse[String] = Http(EXTERNAL_URL + "/api/swift_ref/routing").param("routing_value", routingValue).asString
    if(response.body.equals("")) false else parseJsonString(response.body).contains("countryCode")
  }

  def swiftRoutingVerify(swiftCode:String, routingValue:String) : Boolean = {
    val response: HttpResponse[String] = Http(EXTERNAL_URL + "/api/swift_ref/swift_routing").params(Seq("swift_code" -> swiftCode, "routing_value" -> routingValue)).timeout(connTimeoutMs = 5000, readTimeoutMs = 10000).asString
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

    val swiftCode = "ABKKAEAD4"
    val routingValue = "053112479"

//    val res1: HttpResponse[String] = Http(EXTERNAL_URL + "/api/swift_ref/swift").param("swift_code", swiftCode).asString
//    println(res1.body.equals(""))
//
//    println(parseJsonString(res1.body))
//
//
//    val res2: HttpResponse[Map[String, String]] = Http(EXTERNAL_URL + "/api/swift_ref/routing").param("routing_value", routingValue).asParamMap
//    println(res2.body)
//
//
//    val res3: HttpResponse[Map[String,String]] = Http(EXTERNAL_URL + "/api/swift_ref/swift_routing").params(Seq("swift_code" -> "BOFAUS3N", "routing_value" -> "026009593")).asParamMap
//    println(res3.body)

  }


}
