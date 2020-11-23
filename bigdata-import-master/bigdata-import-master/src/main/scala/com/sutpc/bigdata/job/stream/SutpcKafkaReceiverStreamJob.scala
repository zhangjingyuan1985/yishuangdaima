package com.sutpc.bigdata.job.stream

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}


/**
  * <p>Title: SutpcKafkaReceiverStreamJob</p>
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          Date 2019/7/10  16:19
  * @version V1.0
  */
object SutpcKafkaReceiverStreamJob {

  def main(args: Array[String]): Unit = {

    val app = s"${this.getClass.getSimpleName}".filter(!_.equals('$'))

    Logger.getLogger("org").setLevel(Level.ERROR)

    println("参数验证")
    if (args.length < 5) {
      System.err.println(
        s"""
           |Usage: $app <brokers> <groupId>  <topics> <duration> <hdfsPath>
           |  <brokers> is a list of one or more Kafka brokers
           |  <groupId> is a consumer group name to consume from topics
           |  <topics> is a list of one or more kafka topics to consume from
           |
        """.stripMargin)
      System.exit(1)
    }

    println("参数")
    args.foreach(println)

    val Array(brokers, groupId, topics, duration, hdfsPaths) = args

    println("spark初始化...")
    val sparkConf = new SparkConf()
      .set("spark.shuffle.consolidateFiles", "true")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.streaming.kafka.maxRatePerPartition", "500")
      .set("spark.streaming.stopGracefullyOnShutdown", "true")
      .set("spark.network.timeout", "600")
      .set("spark.streaming.kafka.consumer.poll.ms", "60000")
      .set("spark.core.connection.ack.wait.timeout", "900")
      .set("spark.rpc.message.maxSize", "50")
      .set("spark.akka.timeout", "900")

    val spark = SparkSession.builder().master("yarn").config(sparkConf).appName(app).getOrCreate()

    val ssc = new StreamingContext(spark.sparkContext, Seconds(duration.toInt))

    println("配置kafka参数...")
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.GROUP_ID_CONFIG -> groupId,
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> "true",
      ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG -> "1000",
      //      ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG -> "30000",
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest"
      //      "linger.ms" -> "50",
      //      "acks" -> "all",
      //      "retries" -> 30.toString,
      //      "reconnect.backoff.ms" -> 20000.toString,
      //      "retry.backoff.ms" -> 20000.toString,
      //      "unclean.leader.election.enable" -> false.toString,
      //      "enable.auto.commit" -> false.toString,
      //      "max.in.flight.requests.per.connection" -> 1.toString
    )

    kafkaParams.foreach(println)

    println("开始从kafka消费")
    val messages = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topicsSet, kafkaParams))

    println("过滤解析消息")
    val dsRecord = messages
      .filter(x => !x.value().trim.equals(""))
      .map(x => {
        val arr = x.value().split(",")
        GPSBusRecord(arr(0).toInt, arr(1), arr(2), arr(3), arr(4).toDouble, arr(5).toDouble, arr(6).toDouble, arr(7).toDouble, arr(8).toInt, arr(9).toInt, arr(10))
      })

    dsRecord.cache()

    dsRecord.print(20)

    println("写入到hdfs parquet文件")
    hdfsPaths.split(",").foreach(hdfsPath => {

      dsRecord.foreachRDD(rdd => {

        import spark.implicits._
        val df = rdd.toDS()
        df.cache()

        println("输出")
        df.show()

        println("输出数据量【" + df.count() + "】")

        df.coalesce(1).write.mode("append").partitionBy("date").parquet(hdfsPath)
      })

    })


    ssc.start()
    ssc.awaitTermination()
  }

}

case class GPSBusRecord(date: Int, time: String, companyCode: String, id: String, lon: Double, lat: Double, speed: Double, angle: Double, operationState: Int, dataState: Int, route_id: String)