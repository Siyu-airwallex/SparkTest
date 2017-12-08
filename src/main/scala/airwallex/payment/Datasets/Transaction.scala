package airwallex.payment.Datasets

import org.apache.spark.sql.types.StructField

case class Transaction(paymentId: String,
                       sourceCurrency: String,
                       paymentCurrency: String,
                       feeCurrency: String,
                       sourceAmount: Double,
                       paymentAmount: Double,
                       feeAmount: Double,
                       amountPaidInPaymentCurrency: Double,
                       amountReceivedInSourceCurrency: Double,
                       feeAmountInPaymentCurrency: Double,
                       status: String
                      )
