package airwallex.payment.Datasets

import airwallex.payment.Datasets.LookupType.LookupType
import airwallex.payment.model.BankAccount
import org.apache.spark.sql.SparkSession

/**
  * Created by charlie on 18/12/17.
  */

object LookupType extends Enumeration{
  type LookupType = Value
  val RoutingOnly, SwiftOnly, SwiftRouting = Value
}

object CustomFunctions {

  import CommonFunctions.{routingVerify, swiftVerify, swiftRoutingVerify}


  def fixRoutingLeadingZeros(record: BankAccount): BankAccount = {

    val digits = Map[String, Int]("AU" -> 6, "GG" -> 6, "GB" -> 6,  "HK" -> 3, "IM" -> 6,"CA_1" -> 3, "CA_2" ->5,
                     "JE" -> 6, "SG" -> 4, "US" -> 9)

    def process(country: String , value : String) = {
//      println("origin value: " + value)
//      println("after value: " + "0" * (digits(country) - value.length) + value )
      "0" * (digits(country) - value.length) + value }

    val bankCountryCode = record.bank_country_code
    var routingValue1 = record.account_routing_value1
    var routingValue2 = record.account_routing_value2

    try {
      bankCountryCode match {
        case "AU" | "GG" | "IM" | "JE" | "SG" | "GB" | "US" | "HK" => if(routingValue1.length > 0 && routingValue1.length < digits(bankCountryCode))
                                                                      record.account_routing_value1 = process(bankCountryCode, routingValue1)
        case "CA" =>  { if(routingValue1.length > 0 && routingValue1.length < 3)
                        record.account_routing_value1 = process("CA_1", routingValue1)
                        if(routingValue2.length > 0 && routingValue2.length < 5)
                        record.account_routing_value2 = process("CA_2", routingValue2) }
        case _ =>
      }
    }catch{
      case  npe : NullPointerException => {
        println("bank_country_code: " + bankCountryCode)
        println("account_routing_value1: " + routingValue1)
        println("account_routing_value2: " + routingValue2)

      }

    }
    record
  }


  def lookupTypeResolver(record: BankAccount): LookupType = (record.swift_code, record.account_routing_value1)
    match {
      case ("", _) => LookupType.RoutingOnly
      case (_, "") => LookupType.SwiftOnly
      case _ => LookupType.SwiftRouting
    }


  def swiftRefLookup(record: BankAccount): Boolean = {
    val bankCountryCode = record.bank_country_code
    val accountNumber = record.account_number
    val routingValue1 = record.account_routing_value1
    val routingValue2 = record.account_routing_value2
    val swiftCode = record.swift_code

    val lookupType = lookupTypeResolver(record)

    (bankCountryCode, lookupType) match {
      case (_, LookupType.SwiftOnly) => swiftVerify(swiftCode)
      case ("CA", LookupType.RoutingOnly) => {
        val routingValue = "0" + routingValue1 + routingValue2
        routingVerify(routingValue)
      }
      case ("CA", LookupType.SwiftRouting) => {
        val routingValue = "0" + routingValue1 + routingValue2
        swiftRoutingVerify(swiftCode,routingValue)
      }
      case ("HK", LookupType.RoutingOnly) => {
        val routingValue = routingValue1 + accountNumber.substring(0,3)
        routingVerify(routingValue)
      }
      case ("HK", LookupType.SwiftRouting) => {
        val routingValue = routingValue1 + accountNumber.substring(0,3)
        swiftRoutingVerify(swiftCode,routingValue)
      }
      case(_, LookupType.RoutingOnly) => routingVerify(routingValue1)
      case(_, LookupType.SwiftRouting) => swiftRoutingVerify(swiftCode,routingValue1)
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

    val safeString = spark.udf.register("safeString", CommonFunctions.safeString)

    import spark.implicits._

    val table = spark.read.option("inferSchema", "true")
      .option("header", "true")
      .option("escape", "\"")
      .csv("src/resources/airwallex/Payments.csv")


    val bankAccounts = table.select(
      $"short_reference_id" as "reference_id",
      $"`beneficiary.bank_details.account_name`" as "account_name", // use `backticks` to escape dot
      $"`beneficiary.bank_details.bank_country_code`" as "bank_country_code",
      $"`beneficiary.bank_details.account_currency`" as "account_currency",
      $"`beneficiary.bank_details.account_number`" as "account_number",
      $"`beneficiary.bank_details.account_routing_type1`" as "account_routing_type1",
      $"`beneficiary.bank_details.account_routing_type2`" as "account_routing_type2",
      safeString($"`beneficiary.bank_details.account_routing_value1`") as "account_routing_value1",
      safeString($"`beneficiary.bank_details.account_routing_value2`") as "account_routing_value2",
      $"`beneficiary.bank_details.bank_name`" as "bank_name",
      $"`beneficiary.bank_details.bank_street_address`" as "bank_street_address",
      $"`beneficiary.bank_details.iban`" as "iban",
      safeString($"`beneficiary.bank_details.swift_code`") as "swift_code",
      $"`beneficiary.bank_details.binding_mobile_number`" as "binding_mobile_number",
      $"`beneficiary.bank_details.bank_branch`" as "bank_branch",
      $"payment_method",
      $"status")
      .as[BankAccount]


    bankAccounts.groupBy("status").count().show()

    val successPayments = bankAccounts.filter(record => record.status.equals("DISPATCHED")).persist()

    val failedPayments = bankAccounts.filter(record => record.status.equals("CANCELLED")).persist()




    val swiftPayments = successPayments.map(CustomFunctions.fixRoutingLeadingZeros)
      .filter(CustomFunctions.lookupTypeResolver(_) == LookupType.SwiftOnly).persist()

    val invalidSiwftPaymens = swiftPayments.filter(!CustomFunctions.swiftRefLookup(_)).persist()


    val routingPayments = successPayments.map(CustomFunctions.fixRoutingLeadingZeros)
      .filter(CustomFunctions.lookupTypeResolver(_) == LookupType.RoutingOnly).persist()

    val invalidRoutingPaymens = routingPayments.filter(!CustomFunctions.swiftRefLookup(_)).persist()


    val swiftRoutingPayments = successPayments.map(CustomFunctions.fixRoutingLeadingZeros)
                                              .filter(CustomFunctions.lookupTypeResolver(_) == LookupType.SwiftRouting).persist()

    val invalidSiwftRoutingPaymens = swiftRoutingPayments.filter(!CustomFunctions.swiftRefLookup(_)).persist()




    printf("success swift payments number is : %d\n", swiftPayments.count())

    invalidSiwftPaymens.select($"account_routing_value1", $"account_routing_value2", $"swift_code", $"payment_method")
                              .show()

    invalidSiwftPaymens.coalesce(1).write
                                   .format("csv")
                                   .mode("overwrite")
                                   .option("header", "true")
                                   .save("src/resources/result/invalidSwift.csv")


    printf("success routing payments number is : %d\n", routingPayments.count())

    invalidRoutingPaymens.select($"account_routing_value1", $"account_routing_value2", $"swift_code", $"payment_method")
                              .show()

    invalidRoutingPaymens.coalesce(1).write
                                     .format("csv")
                                     .mode("overwrite")
                                     .option("header", "true")
                                     .save("src/resources/result/invalidRouting.csv")



    printf("success swift_routing payments number is : %d\n", swiftRoutingPayments.count())

    invalidSiwftRoutingPaymens.select($"account_routing_value1", $"account_routing_value2", $"swift_code", $"payment_method")
                              .show()

    invalidSiwftRoutingPaymens.coalesce(1).write
                                          .format("csv")
                                          .mode("overwrite")
                                          .option("header", "true")
                                          .save("src/resources/result/invalidSwiftRouting.csv")


    val invalidSwiftRoutingErrorSwift =  invalidSiwftRoutingPaymens.filter(row => !CommonFunctions.routingVerify(row.swift_code)).persist()


    invalidSwiftRoutingErrorSwift.select($"account_routing_value1", $"account_routing_value2", $"swift_code", $"payment_method").show()

    invalidSwiftRoutingErrorSwift.coalesce(1).write
                                          .format("csv")
                                          .mode("overwrite")
                                          .option("header", "true")
                                          .save("src/resources/result/invalidSwiftRouting.csv/swiftError.csv")


  }
}
