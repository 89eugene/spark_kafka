package otus

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.{PartitionInfo, TopicPartition}
import org.apache.kafka.common.serialization.StringDeserializer

import java.{lang, util}
import java.util.{Collections, Properties}

object KafkaClient extends App {

  private def configuration: Properties = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getCanonicalName)
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getCanonicalName)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "0")
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "15")
    props
  }

  val topic = "books"

  val consumer = new KafkaConsumer[String, String](configuration)
  val topics = List(topic)

  val partitions = consumer.partitionsFor(topic)
  val topicPartitions = new java.util.ArrayList[TopicPartition]

  partitions.forEach(
    part =>
      topicPartitions.add(new TopicPartition(topic, part.partition()))
  )

  val endOffsets: util.Map[TopicPartition, lang.Long] = consumer.endOffsets(topicPartitions)

  consumer.assign(topicPartitions)

  endOffsets.forEach((topicPartition, offset)=> consumer.seek(topicPartition, (offset-5)))

  val res = consumer.poll(1000)

  topicPartitions.forEach(
    p => {
      val recs: util.List[ConsumerRecord[String, String]] = res.records(p)
      recs.forEach(x => println(x))
    })

  consumer.close()
}
