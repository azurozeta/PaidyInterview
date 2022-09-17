package forex.stream

import forex.domain.Timestamp

import java.util.Properties
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.errors.TimeoutException

object RateStream {
  val TOPIC = "rate-updates"
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("max.block.ms", 2000)

  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  def publishRate(json: String): Unit = {
    // Need to figure out how to detect if Kafka Broker is not running then skip
    // For now use timeout in try catch
    try {
      val record = new ProducerRecord(TOPIC, Timestamp.now.value.toString, json)
      val _ = producer.send(record, onCompletion)
    } catch {
      case _: TimeoutException => println("Kafka server is not available.")
    }
  }

  def onCompletion(metadata: RecordMetadata, e: Exception): Unit = {
    if (e != null) e.printStackTrace()
    else println("Sent record offset: " + metadata.offset)
  }
}