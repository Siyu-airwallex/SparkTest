package airwallex.payment.Datasets

import org.apache.spark.sql.SparkSession


object SwiftRefLoadRandomCheck {

  val spark = SparkSession
    .builder()
    .appName("PaymentsBankAccountLookup")
    .master("local")
    .config("spark.debug.maxToStringFields", 100)
    .getOrCreate()

  val safeString = spark.udf.register("safeString", SwiftRefEndpoint.safeString)

  import spark.implicits._

  val table = spark.read.option("inferSchema", "true")
    .option("header", "true")
    .option("escape", "\"")
    .csv("src/resources/airwallex/Payments.csv")

  def main(args: Array[String]): Unit = {

  }

}
