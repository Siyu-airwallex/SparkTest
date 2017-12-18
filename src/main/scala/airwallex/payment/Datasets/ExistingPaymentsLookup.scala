package airwallex.payment.Datasets

import airwallex.payment.model.BankAccount
import org.apache.spark.sql.SparkSession

import scalaj.http.{Http, HttpRequest, HttpResponse}

/**
  * Created by charlie on 18/12/17.
  */
class ExistingPaymentsLookup {


}


object CustomFunctions{

  def swiftVerify(swiftCode:String) : Boolean = {
    val response: HttpResponse[String] = Http("http://localhost/api/swift_ref/swift").param("swift_code", swiftCode).asString
    return if(response.body == null) false else true
  }

  def routingVerify(routingValue:String) : Boolean = {
    val response: HttpResponse[String] = Http("http://localhost/api/swift_ref/routing").param("routing_value", routingValue).asString
    return if(response.body == null) false else true
  }

  def swiftRoutingVerify(swiftCode:String, routingValue:String) : Boolean = {
    val response: HttpResponse[String] = Http("http://localhost/api/swift_ref/swift_routing").params(Seq("swift_code" -> swiftCode, "routing_value" -> routingValue)).asString
    return if(response.body == null) false else true
  }

  def swiftRefLookup(record : BankAccount) : Boolean = {
    val countryCode = record.bank_country_code
    val routingValue1 = record.account_routing_value1
    val routingValue2 = record.account_routing_type2
    val swiftCode = record.swift_code

    (countryCode, routingValue1, routingValue2, swiftCode) match {
      case (_, null, null, swiftCode) => {return swiftVerify(swiftCode)}
      case (_, routingValue1, null, swiftCode) => {return swiftRoutingVerify(swiftCode, routingValue1)}
      case (_, routingValue1, null, null) => {return routingVerify(routingValue1)}
      case _  => false

    }
  }
}

object ExistingPaymentsLookup{

  def main(args: Array[String]): Unit ={

    val spark = SparkSession
      .builder()
      .appName("PaymentsBankAccountLookup")
      .master("local")
      .config("spark.debug.maxToStringFields", 100)
      .getOrCreate()

    import spark.implicits._

    val table = spark.read.option("inferSchema", "true")
      .option("header","true")
      .option("escape", "\"")
      .csv("src/resources/airwallex/Payments.csv")


    val bankAccounts = table.select($"`beneficiary.bank_details.account_name`" as "account_name", // use `backticks` to escape dot
      $"`beneficiary.bank_details.bank_country_code`" as "bank_country_code",
      $"`beneficiary.bank_details.account_currency`" as "account_currency",
      $"`beneficiary.bank_details.account_number`" as "account_number",
      $"`beneficiary.bank_details.account_routing_type1`" as "account_routing_type1",
      $"`beneficiary.bank_details.account_routing_type2`" as "account_routing_type2",
      $"`beneficiary.bank_details.account_routing_value1`" as "account_routing_value1",
      $"`beneficiary.bank_details.account_routing_value2`" as "account_routing_value2",
      $"`beneficiary.bank_details.bank_name`" as "bank_name",
      $"`beneficiary.bank_details.bank_street_address`" as "bank_street_address",
      $"`beneficiary.bank_details.iban`" as "iban",
      $"`beneficiary.bank_details.swift_code`" as "swift_code",
      $"`beneficiary.bank_details.binding_mobile_number`" as "binding_mobile_number",
      $"`beneficiary.bank_details.bank_branch`" as "bank_branch",
      $"status")
      .as[BankAccount]


    bankAccounts.show()

    bankAccounts.filter( record => CustomFunctions.swiftRefLookup(record)).show()


  }
}
