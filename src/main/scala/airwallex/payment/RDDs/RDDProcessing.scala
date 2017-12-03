package airwallex.payment.RDDs
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by charlie on 2/12/17.
  */
object RDDProcessing {

  def main(args : Array[String]) {

    val conf = new SparkConf().setMaster("local").setAppName("PaymentBasicProcessing")

    val sc = new SparkContext(conf)

    val payments = sc.textFile("src/resources/airwallex/Payments.csv")

    val header = payments.first()

    val columns: Array[String] = header.split(",") //define as broadcast variable

    val keyIndex = sc.broadcast(columns.indexOf("status"))


// Test basic operation

    val totalLength = payments.map(s => s.length).reduce(_+_)

    println("Total length : " + totalLength)


//  Count the number by status

    val resultByStatus = payments.filter(row => row != header)
                                .map(row => (row.split(",")(keyIndex.value), 1))
                                    .reduceByKey(_+_).collect()

    resultByStatus.foreach(println)





  }

}


