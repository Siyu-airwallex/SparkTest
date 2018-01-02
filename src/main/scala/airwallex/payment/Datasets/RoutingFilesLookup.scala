package airwallex.payment.Datasets

import java.util

import airwallex.payment.model.BankAccount
import org.apache.spark.sql.{DataFrame, SparkSession}

object RoutingFilesLookup {

  val spark: SparkSession = SparkSession
    .builder()
    .appName("RoutingFilesLookup")
    .master("local")
    .config("spark.debug.maxToStringFields", 50)
    .getOrCreate()

  val bsbDir: DataFrame = spark.read
    .option("escape", "\"")
    .csv("src/resources/airwallex/bsb_directory.csv")

  val abaLocalDir: DataFrame = spark.read
    .option("escape", "\"")
    .option("header", "true")
    .csv("src/resources/airwallex/aba_local_directory.csv")

  val abaSwiftDir: DataFrame = spark.read
    .option("escape", "\"")
    .option("header", "true")
    .csv("src/resources/airwallex/aba_swift_directory.csv")

  val bankCodeDir: DataFrame = spark.read
    .option("escape", "\"")
    .csv("src/resources/airwallex/bank_code_directory.csv")

  def populateBSBList: Array[String] = {
    import spark.implicits._
    bsbDir.map(row => row.getAs[String](0)).map(value => value.replace("-", "")).collect()
  }

  def populateABAList: Array[String] = {
    import spark.implicits._
    val locals = abaLocalDir.map(row => row.getAs[String](0)).collect();
    val swifts = abaSwiftDir.map(row => row.getAs[String](0)).collect();
    locals ++ swifts.distinct
  }

  def populateBankCodeList: Array[String] = {
    import spark.implicits._
    bankCodeDir.map(row => row.getAs[String](1) + SwiftRefEndpoint.safeString(row.getAs[String](3))).collect()
  }


  def main(args: Array[String]): Unit = {

//    populateBSBList.foreach(println(_))

//    populateABAList.foreach(println(_))

//    populateBankCodeList.foreach(println(_))

//    println("bsb values can not be found in database: ")
//    populateBSBList.filter(value => !CommonFunctions.routingVerify(value)).foreach(println)
//
//    println("aba values can not be found in database: ")
//    populateABAList.filter(value => !CommonFunctions.routingVerify(value)).foreach(println)
//
    println("bank_code values can not be found in database: ")
    populateBankCodeList.filter(value => !SwiftRefEndpoint.routingVerify(value)).foreach(println)

//    import spark.implicits._
//    bankCodeDir.map(row => (CommonFunctions.safeString(row.getString(1)),CommonFunctions.safeString(row.getString(3)))).filter(_._2 == "").show()

  }

}
