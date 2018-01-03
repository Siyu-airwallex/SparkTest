package airwallex.payment.Datasets

import java.util

import airwallex.payment.model.BankAccount
import groovy.sql.DataSet
import org.apache.spark.sql._

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
    .option("header", "true")
    .csv("src/resources/airwallex/bank_code_directory.csv")

  def populateBSBList: Dataset[String] = {
    import spark.implicits._
    bsbDir.map(row => row.getAs[String](0)).map(value => value.replace("-", "")).distinct()
  }

  def populateABAList: Dataset[String] = {
    import spark.implicits._
    val locals = abaLocalDir.map(row => row.getAs[String]("Routing Number"))
    val swifts = abaSwiftDir.map(row => row.getAs[String]("FedRoutingNR"))
    locals.union(swifts).distinct()
  }

  def populateBankCodeList: Dataset[String] = {
    import spark.implicits._
    bankCodeDir.map(row => row.getAs[String]("Clearing Code") + SwiftRefEndpoint.safeString(row.getAs[String]("Branch Code"))).distinct()
  }


  def main(args: Array[String]): Unit = {

    println("Total number of abas in routing file", populateABAList.count())

    println("Total number of bsbs in routing file", populateBSBList.count())

    println("Total number of bankCodes in routing file", populateBankCodeList.count())


    println("aba values can not be found in swift ref database: ")
    val abaNotFoundInSwiftRefData = populateABAList.filter(!SwiftRefEndpoint.routingVerify(_))
    abaNotFoundInSwiftRefData.coalesce(1).write
                                          .format("csv")
                                          .mode("overwrite")
                                          .option("header", "true")
                                          .save("src/resources/result/abaNotFoundInSwiftRef.csv")


    println("bsb values can not be found in swift ref database: ")
    val bsbNotFoundInSwiftRefData = populateBSBList.filter(!SwiftRefEndpoint.routingVerify(_))
    bsbNotFoundInSwiftRefData.coalesce(1).write
                                        .format("csv")
                                        .mode("overwrite")
                                        .option("header", "true")
                                        .save("src/resources/result/bsbNotFoundInSwiftRef.csv")


    println("bankCode values can not be found in swift ref database: ")
    val bankCodeNotFoundInSwiftRefData = populateBankCodeList.filter(!SwiftRefEndpoint.routingVerify(_))
    bankCodeNotFoundInSwiftRefData.coalesce(1).write
                                              .format("csv")
                                              .mode("overwrite")
                                              .option("header", "true")
                                              .save("src/resources/result/bankCodeNotFoundInSwiftRef.csv")

  }

}
