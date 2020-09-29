// kafka stuff

import org.apache.kafka.clients.producer.Callback;

import org.apache.kafka.clients.producer.KafkaProducer;

import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.kafka.clients.producer.RecordMetadata;

import org.apache.kafka.clients.admin._;

import java.util.Properties;

import org.apache.kafka.common.config.TopicConfig;

 

// avro stuff

import org.apache.avro.Schema

import org.apache.avro.Schema.Parser

import org.apache.avro.generic.GenericData

import org.apache.avro.generic.GenericRecord

import org.apache.avro.specific.SpecificDatumWriter

import java.io.ByteArrayOutputStream

import org.apache.avro.io._

 

// other stuff

import collection.JavaConverters._

 

 

 

val schemaStr = """{

                                                           "namespace": "bandcamp.test",

                                                            "type": "record",

                                                            "name": "user",

                                                            "fields": [{  "name": "band_name", "type": "string"} ]             }"""

 

val schema: Schema = new Schema.Parser().parse( schemaStr )

 

val record: GenericRecord = new GenericData.Record(schema)

 

record.put( "band_name", "denmark" )

 

// Serialize generic record into byte array -- copied from DZone

val writer = new SpecificDatumWriter[GenericRecord](schema)

val out = new ByteArrayOutputStream()

val encoder: BinaryEncoder = EncoderFactory.get().binaryEncoder(out, null)

writer.write( record , encoder)

encoder.flush()

out.close()

val serializedBytes: Array[Byte] = out.toByteArray()

 

 

val properties : Properties = new Properties();

properties.put("bootstrap.servers", "kafka:29092");

 

val adminClient = AdminClient.create(properties)

 

properties.put("client.id", "SampleProducer");

properties.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

properties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

 

val configs = Map(TopicConfig.CLEANUP_POLICY_CONFIG -> TopicConfig.CLEANUP_POLICY_COMPACT)

 

 

val topic = new NewTopic("test_topic", 1,1)

 

topic.configs(configs.asJava)

 

adminClient.createTopics( List( topic ).asJavaCollection );

 
/*

val currTopicsFetch = adminClient.listTopics(new ListTopicsOptions().timeoutMs(10000).listInternal(true))

val currTopics = currTopicsFetch.namesToListings().get().asScala

println(currTopics)

var i = 0

for (res <- currTopics) {

    i+=1

    println(res._1) }

 

 

val producer = new KafkaProducer[String, Array[Byte]](properties);

 

producer.send(new ProducerRecord("test_topic1",serializedBytes)).get();
*/
