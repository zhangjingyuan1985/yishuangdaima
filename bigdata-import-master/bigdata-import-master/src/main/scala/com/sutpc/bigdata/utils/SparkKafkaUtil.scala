package com.sutpc.bigdata.utils

import com.sutpc.bigdata.kafka.KafkaSink
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

import scala.collection.Map

/**
  * <p>Description:TODO  </p>
  * <p>Copyright: Copyright (c) 2019</p>
  * <p>Company: 深圳市城市交通规划研究中心</p>
  *
  * @author zhangyongtian
  * @version 1.0.0
  *          date 2019/10/18  15:40
  */
class SparkKafkaUtil(brokers: String, topic: String) extends Serializable {

  def input(ssc: StreamingContext, groupId: String): DStream[ConsumerRecord[String, String]] = {

    val inputStream = {
      val kafkaParams = Map[String, Object](
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
        ConsumerConfig.GROUP_ID_CONFIG -> groupId,
        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> "true",
        ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG -> "1000",
        ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG -> "30000",
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
        //        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest",
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "latest"
        //        ConsumerConfig.FETCH_MAX_BYTES_CONFIG -> "1024"
        //        "linger.ms" -> "50",
        //        "acks" -> "all",
        //        "retries" -> 30.toString,
        //        "reconnect.backoff.ms" -> 20000.toString,
        //        "retry.backoff.ms" -> 20000.toString,
        //        "unclean.leader.election.enable" -> false.toString,
        //        "enable.auto.commit" -> false.toString,
        //        "max.in.flight.requests.per.connection" -> 1.toString
      )

      KafkaUtils.createDirectStream[String, String](ssc,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String, String](topic.split(",").toSet, kafkaParams))
    }

    inputStream
  }

  def output[T](spark: SparkSession, ds: Dataset[T]) = {
    println("输出到kafka")
    ds.show(false)
    val out_config = spark.sparkContext.broadcast(
      Map[String, Object](
        "bootstrap.servers" -> brokers,
        ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG -> "30000",
        "key.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
        "value.serializer" -> "org.apache.kafka.common.serialization.StringSerializer"))

    ds.foreachPartition { it =>
      val sink = KafkaSink[String, T](out_config.value)
      it.foreach(v => sink.send(topic, v))
      sink.producer.close()
    }

    //    ds.write
    //      .format("kafka")
    //      .option("kafka.bootstrap.servers", brokers)
    //      .option("topic", topic)
    //      .option("checkpointLocation", "data/checkpoint/" + System.currentTimeMillis())


  }
}

