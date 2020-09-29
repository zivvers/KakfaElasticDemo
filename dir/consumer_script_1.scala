// kafka stuff


//import kafka.consumer.{Consumer, ConsumerConfig, ConsumerTimeoutException, Whitelist} // don't work!

import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Properties;

// producer stuff
/* import org.apache.kafka.clients.producer.Callback;

import org.apache.kafka.clients.producer.KafkaProducer;

import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.kafka.clients.producer.RecordMetadata;

import org.apache.kafka.clients.admin._;

import java.util.Properties;

import org.apache.kafka.common.config.TopicConfig; */

 

// avro stuff

import org.apache.avro.Schema

import org.apache.avro.Schema.Parser

import org.apache.avro.generic.GenericData

import org.apache.avro.generic.GenericRecord

import org.apache.avro.specific.SpecificDatumWriter
import org.apache.avro.specific.SpecificDatumReader

import java.io.ByteArrayOutputStream

import org.apache.avro.io._

 

// other stuff

import collection.JavaConverters._


// deserialize byte array message
def getBand(message: Array[Byte], schema: Schema) = {
 // Deserialize and create generic record
 val reader: DatumReader[GenericRecord] = new SpecificDatumReader[GenericRecord](schema)

 val decoder: Decoder = DecoderFactory.get().binaryDecoder(message, null)

 val bandData: GenericRecord = reader.read(null, decoder)
 // Make user object
 //val user = User(userData.get("id").toString.toInt, userData.get("name").toString, 

 /*try {

 	Some(bandData.get("band_name").toString)

 } catch {

 case _ => None
 })*/
 Some(bandData)
}


 


val schemaStr = """{

                                                           "namespace": "bandcamp.test",

                                                            "type": "record",

                                                            "name": "user",

                                                            "fields": [{  "name": "band_name", "type": "string"} ]             }"""

 

val schema: Schema = new Schema.Parser().parse( schemaStr )

val properties : Properties = new Properties();

properties.put("bootstrap.servers", "kafka:29092");

properties.put("client.id", "SampleConsumer");


properties.put("group.id", "Ziv-Group");

properties.put("zookeeper.connect", "zookeeper:2181")

properties.put("key.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");

properties.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");

val topic = "test_topic1";



//val configs = Map(TopicConfig.CLEANUP_POLICY_CONFIG -> TopicConfig.CLEANUP_POLICY_COMPACT)


val consumer = new KafkaConsumer[String, Array[Byte]](properties);


consumer.subscribe( List( topic ).asJavaCollection );



while (true) {

  val record = consumer.poll(1000).asScala
  for (data <- record.iterator) {

  	var band = getBand(data.value(), schema)
    println( band )

  }

}
 

//val record: GenericRecord = new GenericData.Record(schema)

 

//record.put( "band_name", "denmark" )

 

// Serialize generic record into byte array -- copied from DZone


/*
val writer = new SpecificDatumWriter[GenericRecord](schema)

val out = new ByteArrayOutputStream()

val encoder: BinaryEncoder = EncoderFactory.get().binaryEncoder(out, null)

writer.write( record , encoder)

encoder.flush()

out.close()

val serializedBytes: Array[Byte] = out.toByteArray() */

 


 

 

//val topic = new NewTopic("test_topic1", 1,1)

 

//topic.configs(configs.asJava)

 

//adminClient.createTopics( List( topic ).asJavaCollection );

 
/*
val currTopicsFetch = adminClient.listTopics(new ListTopicsOptions().timeoutMs(10000).listInternal(true))

 

val currTopics = currTopicsFetch.namesToListings().get().asScala

println(currTopics)

var i = 0

for (res <- currTopics) {

    i+=1

    println(res._1) }

 
*/
 

//val producer = new KafkaProducer[String, Array[Byte]](properties);

 

//producer.send(new ProducerRecord("test_topic1",serializedBytes)).get();
