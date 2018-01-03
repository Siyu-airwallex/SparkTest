package airwallex.payment.Datasets

import org.json4s.jackson.JsonMethods.parse

import scalaj.http.{Http, HttpResponse}

object RoutingEndpoint {

  val EXTERNAL_URL = "http://localhost:10080"
  val ROUTING_CODE_PATH = "/api/routing_code/verify"
  val ROUTING_PAIR_PATH = "/api/routing_pair/verify"

  def abaVerify(aba:String) : Boolean = {
    val localResponse: HttpResponse[String] = Http(EXTERNAL_URL + ROUTING_CODE_PATH).params(Seq("country_code" -> "US", "routing_type" -> "aba", "routing_value" -> aba, "payment_method" -> "LOCAL")).asString
    val swiftResponse: HttpResponse[String] = Http(EXTERNAL_URL + ROUTING_CODE_PATH).params(Seq("country_code" -> "US", "routing_type" -> "aba", "routing_value" -> aba, "payment_method" -> "SWIFT")).asString
    localResponse.body.toBoolean || swiftResponse.body.toBoolean
  }

  def bsbVerify(bsb:String) : Boolean = {
    val response: HttpResponse[String] = Http(EXTERNAL_URL + ROUTING_CODE_PATH).params(Seq("country_code" -> "AU", "routing_type" -> "bsb", "routing_value" -> bsb, "payment_method" -> "LOCAL")).asString
    response.body.toBoolean
  }

  def bankCodeVerify(bankCode:String, branchCode:String) : Boolean = {
    val response: HttpResponse[String] = Http(EXTERNAL_URL + ROUTING_PAIR_PATH).params(Seq("country_code" -> "HK", "bank_code" -> bankCode, "branch_code" -> branchCode )).asString
    response.body.toBoolean
  }

  def main(args: Array[String]): Unit = {

    println(abaVerify("021583153"))
    println(bsbVerify("012019"))
    println(bankCodeVerify("041","266"))

  }

}
