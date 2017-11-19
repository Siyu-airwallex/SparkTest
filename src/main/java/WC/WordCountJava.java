package WC;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.Arrays;

/**
 * Created by charlie on 15/11/17.
 */
public class WordCountJava {


    private static final Logger logger = LoggerFactory.getLogger(WordCountJava.class);
    private static final String filePath = "src/resources/words";

    public void run(String inputFile) {

        //String master_cluster = "spark://172.16.10.138:7077";
        //SparkConf conf = new SparkConf().setJars(new String[]{"target/SparkTest-1.0-SNAPSHOT.jar"}).setMaster(master_cluster).setAppName("Word Count");
        String master_local = "local[*]";
        SparkConf conf = new SparkConf().setMaster(master_local).setAppName("Word Count");
        // Create a Java version of the Spark Context
        JavaSparkContext sc = new JavaSparkContext(conf);

        // Load the text into a Spark RDD, which is a distributed representation of each line of text
        JavaRDD<String> textFile = sc.textFile(inputFile);
        JavaPairRDD<String, Integer> counts = textFile
                .flatMap(s -> Arrays.asList(s.split(",")).iterator())
                .mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey((a, b) -> a + b);
        counts.foreach(p -> System.out.println(p));
        System.out.println("Total words: " + counts.collect());
//        counts.saveAsTextFile("src/resources/result/");
        sc.close();

    }

    public static void main(String[] args) {

            new WordCountJava().run(filePath);
    }

}
