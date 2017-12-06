package airwallex.payment.Datasets

import org.apache.spark.sql.SparkSession

object DatasetProcessing {

  def main(args: Array[String]): Unit ={

    val spark = SparkSession
                  .builder()
                    .appName("PaymentBasicProcessing")
                        .master("local")
                            .getOrCreate()


    val table = spark.read.csv("src/resources/airwallex/Payments.csv")

    table.select("status").show()

  }

}
