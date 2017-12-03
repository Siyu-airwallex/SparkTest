package airwallex.payment.RDDs

import org.apache.spark.rdd.RDD

/**
  * Created by charlie on 2/12/17.
  */
object CustomFunctions {

  def func1(s: String) : String = {
    ""
  }

  def filterHeader(rdd : RDD[String]) : RDD[String] = {
    val header = rdd.first()
    return rdd.filter(line => line != header)

  }

}
