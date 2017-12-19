package airwallex.payment.Datasets

import airwallex.payment.model.BankAccount
import org.apache.spark.sql.SparkSession

/**
  * Created by charlie on 18/12/17.
  */
class ExistingPaymentsLookup {


}

object CustomFunctions {

  import CommonFunctions.{routingVerify, swiftVerify, swiftRoutingVerify}

  def swiftRefLookup(record: BankAccount): Boolean = {
    val bankCountryCode = record.bank_country_code
    val accountCurrency = record.account_currency
    val accountNumber = record.account_number
    val routingValue1 = record.account_routing_value1
    val routingValue2 = record.account_routing_value2
    val swiftCode = record.swift_code
    val paymentMethod = record.payment_method

    (bankCountryCode, accountCurrency, paymentMethod) match {
      case ("AU", _, "LOCAL") => routingVerify(routingValue1)
      case ("AU", _, "SWIFT") => swiftRoutingVerify(swiftCode, routingValue1)
      case ("CA", _, _) => {
        val routingValue = "0" + routingValue1 + routingValue2
        swiftRoutingVerify(swiftCode, routingValue)
      }
      case ("GG", _, "LOCAL") => routingVerify(routingValue1)
      case ("GG", _, "SWIFT") => swiftRoutingVerify(swiftCode,routingValue1)
      case ("HK", _, "LOCAL") => {
        val routingValue = routingValue1 + accountNumber.substring(0,3)
        routingVerify(routingValue)
      }
      case ("HK", _, "SWIFT") => {
        val routingValue = routingValue1 + accountNumber.substring(0,3)
        swiftRoutingVerify(swiftCode, routingValue)
      }
      case ("IM", _, "LOCAL") => routingVerify(routingValue1)
      case ("IM", _, "SWIFT") => swiftRoutingVerify(swiftCode, routingValue1)
      case ("JE", _, "LOCAL") => routingVerify(routingValue1)
      case ("JE", _, "SWIFT") => swiftRoutingVerify(swiftCode, routingValue1)
      case ("MX", _, _) => routingVerify(routingValue1)
      case ("SG", _, _) => swiftRoutingVerify(swiftCode, routingValue1)
      case ("US", _, "LOCAL") => routingVerify(routingValue1)
      case ("US", _, "SWIFT") => swiftRoutingVerify(swiftCode, routingValue1)
      case ("GB", "GBP", "LOCAL") => routingVerify(routingValue1)
      case ("GB", "GBP", "SWIFT") => swiftRoutingVerify(swiftCode, routingValue1)
      case _ => swiftVerify(swiftCode)
    }
  }
}

object ExistingPaymentsLookup {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder()
      .appName("PaymentsBankAccountLookup")
      .master("local")
      .config("spark.debug.maxToStringFields", 100)
      .getOrCreate()

    import spark.implicits._

    val table = spark.read.option("inferSchema", "true")
      .option("header", "true")
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
      $"payment_method",
      $"status")
      .as[BankAccount]


    bankAccounts.groupBy("status").count().show()

    val successPayments = bankAccounts.filter(record => record.status.equals("DISPATCHED")).persist()

    val failedPayments = bankAccounts.filter(record => record.status.equals("CANCELLED")).persist()



    println(successPayments.filter(record => CustomFunctions.swiftRefLookup(record)).count())
//    failedPayments.filter(record => CustomFunctions.swiftRefLookup(record)).count()



  }
}
