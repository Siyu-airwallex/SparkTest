package airwallex.payment.model

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
