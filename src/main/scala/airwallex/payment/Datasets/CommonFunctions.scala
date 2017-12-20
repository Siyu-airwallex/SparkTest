package airwallex.payment.Datasets

import scalaj.http.{Http, HttpResponse}

object CommonFunctions {

  val EXTERNAL_URL = "http://localhost:10080"

  def swiftVerify(swiftCode:String) : Boolean = {
    val response: HttpResponse[String] = Http(EXTERNAL_URL + "/api/swift_ref/swift").param("swift_code", swiftCode).asString
    if(response.body.equals("")) false else true
  }

  def routingVerify(routingValue:String) : Boolean = {
    val response: HttpResponse[String] = Http(EXTERNAL_URL + "/api/swift_ref/routing").param("routing_value", routingValue).asString
    if(response.body.equals("")) false else true
  }

  def swiftRoutingVerify(swiftCode:String, routingValue:String) : Boolean = {
    val response: HttpResponse[String] = Http(EXTERNAL_URL + "/api/swift_ref/swift_routing").params(Seq("swift_code" -> swiftCode, "routing_value" -> routingValue)).asString
    if(response.body.equals("")) false else true
  }

  def main(args: Array[String]): Unit = {

    printf("swift test works %s \n" , swiftVerify("ABKKAEAD"))
    printf("routing test works %s \n" , routingVerify("053112479"))
    printf("swift_routing test works %s \n" , swiftRoutingVerify("BOFAUS3N", "026009593"))


  }


}