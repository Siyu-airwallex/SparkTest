package airwallex.payment.JDBC

import airwallex.payment.Datasets.RoutingEndpoint
import org.apache.spark.sql.{Encoders, SparkSession}

object RoutingCustomFunctions {

  def mapToRoutingPair(code:String) : (String, String) =
    code.length match {
      case 6 => (code.substring(0,3), code.substring(3,6))
      case 3 => (code, "")
      case 0 => ("", "")
      case _ => (code.substring(0,3), code.substring(3, code.length))
    }

}

object JDBC2Postgres {

  val spark: SparkSession = SparkSession
                            .builder()
                            .appName("PostgresReverseLookup")
                            .master("local")
                            .config("spark.debug.maxToStringFields", 50)
                            .getOrCreate()

  val jdbcPostgres = spark.read
                          .format("jdbc")
                          .option("url", "jdbc:postgresql://localhost:5432/validation")
                          .option("driver", "org.postgresql.Driver")
                          .option("dbtable", "public.bankdirectory")
                          .option("user", "postgres")
                          .option("password", "postgres")
                          .load()

  val bankDirectory = jdbcPostgres.createOrReplaceTempView("bankdirectory")

  val abaDirectory = spark.sql("SELECT national_id FROM bankDirectory WHERE iso_country_code='US' ").distinct()

  val bsbDirectory = spark.sql("SELECT national_id FROM bankDirectory WHERE iso_country_code='AU' ").distinct()

  val bankCodeDirectory = spark.sql("SELECT national_id FROM bankDirectory WHERE iso_country_code='HK' ").distinct()

  def main(args: Array[String]): Unit = {

//      jdbcPostgres.printSchema()

      println("Total number of abas in swift ref database: " + abaDirectory.count())

      println("Total number of bsbs in swift ref database: " + bsbDirectory.count())

      println("Total number of bankCodes in swift ref database: " + bankCodeDirectory.count())

      println("aba values can not be found in routing file: ")
      val abaNotFoundInRoutingFile = abaDirectory.as(Encoders.STRING).filter(!RoutingEndpoint.abaVerify(_))
      abaNotFoundInRoutingFile.coalesce(1).write
                                          .format("csv")
                                          .mode("overwrite")
                                          .option("header", "true")
                                          .save("src/resources/result/abaNotFoundInRoutingFile.csv")


      println("bsb values can not be found in routing file: ")
      val bsbNotFoundInRoutingFile = bsbDirectory.as(Encoders.STRING).filter(!RoutingEndpoint.bsbVerify(_))
      bsbNotFoundInRoutingFile.coalesce(1).write
                                          .format("csv")
                                          .mode("overwrite")
                                          .option("header", "true")
                                          .save("src/resources/result/bsbNotFoundInRoutingFile.csv")


      println("bankCode values can not be found in routing file: ")
      import spark.implicits._
      val bankCodeNotFoundInRoutingFile = bankCodeDirectory.as(Encoders.STRING)
                                                    .filter(pair => {
                                                      val (bankCode, branchCode) = RoutingCustomFunctions.mapToRoutingPair(pair)
                                                      !RoutingEndpoint.bankCodeVerify(bankCode,branchCode)
                                                    })
      bankCodeNotFoundInRoutingFile.coalesce(1).write
                                                .format("csv")
                                                .mode("overwrite")
                                                .option("header", "true")
                                                .save("src/resources/result/bankCodeNotFoundInRoutingFile.csv")


  }

}
