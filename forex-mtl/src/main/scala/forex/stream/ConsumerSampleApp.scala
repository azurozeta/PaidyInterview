import java.util
import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.concurrent.duration.DurationInt
import scala.jdk.CollectionConverters._
import scala.jdk.DurationConverters._

object ConsumerSampleApp extends App {

  import java.util.Properties

  val TOPIC = "rate-updates"

  val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")

  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("group.id", "client-01")

  val consumer = new KafkaConsumer[String, String](props)

  consumer.subscribe(util.Collections.singletonList(TOPIC))

  while(true){
    val records = consumer.poll(10.seconds.toJava)
    for (record <- records.asScala){
     println(record)
    }
  }
}