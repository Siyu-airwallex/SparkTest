package others;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Arrays;
import java.util.List;

/**
 * Created by charlie on 15/11/17.
 */
public class Stock {

    public void dailyStockReturn(String file){

        String master_local = "local[*]";
        SparkConf conf = new SparkConf().setMaster(master_local).setAppName("Daily Stock Return");
        // Create a Java version of the Spark Context
        JavaSparkContext sc = new JavaSparkContext(conf);

        // Load the text into a Spark RDD, which is a distributed representation of each line of text
        long count = sc.textFile(file).count();
        JavaRDD<String> rdd1 = sc.textFile(file).zipWithIndex().filter(pair -> pair._2 < count-1).map(pair -> pair._1);
        JavaRDD<String> rdd2 = sc.textFile(file).zipWithIndex().filter(pair -> pair._2 > 0).map(pair -> pair._1);
        JavaRDD<String> result = rdd1.zip(rdd2).filter(pair-> {
            String[] col1 = pair._1.split(",");
            String[] col2 = pair._2.split(",");
            return col1[2].equals(col2[2]);
        }).map(pair -> {
            Double before = Double.parseDouble(pair._1.split(",")[1]);
            Double current = Double.parseDouble(pair._2.split(",")[1]);
            Double diff = current - before;
            List<String> list = Arrays.asList(pair._2.split(",")[0], diff.toString(), pair._2.split(",")[2]);
            return  String.join(",", list);
        });


//        JavaRDD<String> file2 = file1.
//
//
//
//
//        JavaPairRDD<String, Integer> counts = textFile
//                .flatMap(s -> Arrays.asList(s.split(",")).iterator())
//                .mapToPair(word -> new Tuple2<>(word, 1))
//                .reduceByKey((a, b) -> a + b);
//        counts.foreach(p -> System.out.println(p));
//        System.out.println("daily stock return : " + counts.collect());
//        sc.close();
    }

}
