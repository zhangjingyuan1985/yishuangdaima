package com.sutpc.bigdata.job.stream

import com.sutpc.bigdata.executor.stream.GpsDriveBaiduSampleExecutor
import com.sutpc.bigdata.utils.SparkKafkaUtil
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/11/8  17:30
  */
object GpsBaiduDriveStreamJob {

  def main(args: Array[String]): Unit = {

    val app = s"${this.getClass.getSimpleName}".filter(!_.equals('$'))

    Logger.getLogger("org").setLevel(Level.ERROR)

    println("参数验证")
    if (args.length < 5) {
      System.err.println(
        s"""
           |Usage: $app <brokers> <groupId>  <topic>  <batchDuration>  <outputTable>
           |  <brokers> is a list of one or more Kafka brokers
           |  <groupId> is a consumer group name to consume from topics
           |  <topics> is a list of one or more kafka topics to consume from
           |  <batchDuration> [seconds]
           |  <outputTable>
                """.stripMargin)
      System.exit(1)
    }

    val Array(brokers, groupId, topics, batchDuration, outputTable) = args
    //    val Array(brokers, groupId, topics, batchDuration, outputTable) = Array("10.10.201.44:9092,10.10.201.45:9092,10.10.201.46:9092", "group_spark", "tp_baidu_nav_origin", "10", "transpaas_std_dev.s_vehicle_gps_drive_baidu_real")


    println("参数")
    args.foreach(println)


    println("spark初始化")
    val sparkConf = new SparkConf().setAppName(app)
    //      .setMaster("local[*]")

    sparkConf
      .set("spark.shuffle.consolidateFiles", "true")
      .set("spark.network.timeout", "600")
      .set("spark.streaming.kafka.consumer.poll.ms", "60000")
      .set("spark.core.connection.ack.wait.timeout", "900")
      .set("spark.akka.timeout", "900")
      .set("spark.streaming.backpressure.enabled", "true")
      .set("spark.rpc.message.maxSize", "1000")
      .set("spark.streaming.receiver.maxRate", "1000")
      .set("spark.streaming.backpressure.initialRate", "20000")
      .set("spark.streaming.kafka.maxRatePerPartition", "20000")
      .set("spark.streaming.stopGracefullyOnShutdown", "true")
      .set("fetch.message.max.bytes", "52428800")


    val ssc = new StreamingContext(sparkConf, Seconds(batchDuration.trim.toInt))

    val sc = ssc.sparkContext

    val spark: SparkSession = SparkSession.builder
      .appName("DynamicWarning")
      //      .config("spark.speculation", "true")
      //      .config("spark.speculation.interval", "300s")
      //      .config("spark.speculation.quantile", "0.9")

      .getOrCreate()

    //    spark.sparkContext.getConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer").registerKryoClasses(Array[Class[_]](classOf[ConsumerRecord[String,String]]))

    println("输入：消费kafka")
    val inputStream = new SparkKafkaUtil(brokers, topics).input(ssc, groupId).map(_.value.toString)

    println("*****kafka输入********")

    //    inputStream.cache()
    //    inputStream.foreachRDD(r => println("filteredStream输入数据量：" + r.count()))
    //    inputStream.print()

    outputTable match {
      case "transpaas_std_dev.s_vehicle_gps_drive_real" => {
        new GpsDriveBaiduSampleExecutor(spark).run(inputStream, outputTable)
      }
      case _ => "找不到目标表"
    }


    ssc.start()
    ssc.awaitTermination()
  }

}
