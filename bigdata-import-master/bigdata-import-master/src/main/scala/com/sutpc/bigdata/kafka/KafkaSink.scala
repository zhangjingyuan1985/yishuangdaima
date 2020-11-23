package com.sutpc.bigdata.kafka

import java.util.concurrent.Future

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}

import scala.collection.Map

class KafkaSink[K, V](creator: () => KafkaProducer[K, V]) extends Serializable {
  lazy val producer = creator()

  def send(topic: String, key: K, value: V): Future[RecordMetadata] =
    producer.send(new ProducerRecord[K, V](topic, key, value))

  def send(topic: String, value: V): Future[RecordMetadata] =
    producer.send(new ProducerRecord[K, V](topic, value))
}

// Kafka消息写入
object KafkaSink {

  import scala.collection.JavaConversions._

  def apply[K, V](config: java.util.Properties): KafkaSink[K, V] = apply(config.toMap)

  def apply[K, V](config: Map[String, Object]): KafkaSink[K, V] = {
    val creator = () => {
      val producer = new KafkaProducer[K, V](config)
      sys.addShutdownHook(producer.close())
      producer
    }
    new KafkaSink(creator)
  }
}
