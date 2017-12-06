package airwallex.payment.RDDs


import java.io.File

import com.github.tototoshi.csv.{CSVFormat, CSVReader, DefaultCSVFormat, Quoting}
import com.univocity.parsers.csv.CsvFormat



/**
  * Created by charlie on 3/12/17.
  */
object CSVParserTest {

  def main(args: Array[String]) {

    implicit object MyFormat extends DefaultCSVFormat {
      override val delimiter: Char = ','
      override val quoteChar: Char = '"'
    }

    val reader = CSVReader.open("src/resources/airwallex/Payments.csv")(MyFormat)

    val statusIndex = reader.iterator.next().indexOf("status")

    println(statusIndex)

    reader.toStream.map(list => (list(statusIndex), 1))
                      .groupBy(pair => pair._1)
                          .mapValues(_.map(_._2).sum)
                              .foreach(println)


  }

}
