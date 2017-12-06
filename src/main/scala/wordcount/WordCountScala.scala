package wordcount

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by charlie on 19/11/17.
  */
object WordCountScala {
  def main(args : Array[String])  {
    val conf = new SparkConf()
                  .setMaster("local")
                  .setAppName("WordCountJava")

    val sc = new SparkContext(conf)

    val data = sc.textFile("src/resources/words")

    data.flatMap(_.split(","))
          .map((_,1))
            .reduceByKey(_+_)
              .collect()
                .foreach(println)
  }
}
