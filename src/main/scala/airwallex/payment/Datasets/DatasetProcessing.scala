package airwallex.payment.Datasets
import airwallex.payment.model.Transaction
import org.apache.calcite.avatica.ColumnMetaData.StructType
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.{Encoders, SparkSession}


object DatasetProcessing {

  def main(args: Array[String]): Unit ={

    val spark = SparkSession
                  .builder()
                    .appName("PaymentBasicProcessing")
                        .master("local")
                            .getOrCreate()

    import spark.implicits._

    val table = spark.read.option("inferSchema", "true")
                          .option("header","true")
                          .option("escape", "\"")
                          .csv("src/resources/airwallex/Payments.csv")


    table.groupBy("status").count().show()

    val transaction = table.select($"payment_id" as "paymentId",
                                   $"source_currency" as "sourceCurrency",
                                   $"payment_currency" as "paymentCurrency",
                                   $"fee_currency" as "feeCurrency",
                                   $"source_amount" as "sourceAmount",
                                   $"payment_amount" as "paymentAmount",
                                   $"fee_amount" as "feeAmount",
                                   $"amount_payer_pays_in_payment_currency" as "amountPaidInPaymentCurrency",
                                   $"amount_beneficiary_receives_in_source_currency" as "amountReceivedInSourceCurrency",
                                   $"fee_amount_in_payment_currency" as "feeAmountInPaymentCurrency",
                                   $"status")
                              .as[Transaction]


    transaction.show()


  }

}
