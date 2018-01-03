package airwallex.payment.Datasets

import org.apache.spark.sql.SparkSession

object SQLQuries {

  def main(args: Array[String]): Unit ={

    val spark = SparkSession
                .builder()
                .appName("PaymentBasicProcessing")
                .master("local")
                .getOrCreate()

    val table = spark.read.option("header","true")
                          .option("escape", "\"")
                          .csv("src/resources/airwallex/Payments.csv")

    table.printSchema()

    table.createOrReplaceTempView("payments")

    spark.sql("SELECT amount_payer_pays, amount_beneficiary_receives_in_source_currency, fee_amount, fee_currency, payment_currency, status FROM payments").show()

  }

}
