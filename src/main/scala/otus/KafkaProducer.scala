package otus

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.apache.commons.csv.{CSVFormat, CSVParser}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

import java.io.FileReader
import scala.collection.immutable.ListSet


object KafkaProducer extends App {

  case class Seller(name: String,
                    Author: String,
                    User_Rating: String,
                    reviews: BigDecimal,
                    price: Int,
                    year: Int,
                    genre: String
                   )


  val file = new FileReader("src/main/resources/bestsellers.csv")
  val records: CSVParser = CSVFormat.DEFAULT.builder()
    .setHeader()
    .setSkipHeaderRecord(true)
    .build()
    .parse(file)

  import scala.collection.JavaConversions._


  var values: ListSet[Seller] = new ListSet[Seller]
  for (record <- records) {
    val name = record.get(0)
    val author = record.get(1)
    val userRating = record.get(2)
    val reviews = record.get(3).toInt
    val price = record.get(4).toInt
    val year = record.get(5).toInt
    val genre = record.get(6)
    values += (Seller(name, author, userRating, reviews, price, year, genre))
  }

  implicit val sellerEncoder: Encoder[Seller] = deriveEncoder

  //define producer properties
  val kafkaProps = new java.util.Properties()
  kafkaProps.put("bootstrap.servers", "localhost:29092")
  kafkaProps.put("client.id", "KafkaProducer")
  kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getCanonicalName)
  kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getCanonicalName)
  val topic = "books"
  val kafkaProducer = new KafkaProducer[String, String](kafkaProps)


  def sendKafkaMessage(seller: Seller): Unit = {
    val rec = new ProducerRecord[String, String](topic, sellerEncoder.apply(seller).toString)
    kafkaProducer.send(rec)
  }

  values.foreach(x=>sendKafkaMessage(x))
  kafkaProducer.close()

}
