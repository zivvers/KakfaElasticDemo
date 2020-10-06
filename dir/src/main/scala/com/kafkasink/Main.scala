package com.kafkasink


import scala.io.Source

// kafka stuff

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.admin._;
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}

import java.util.Properties;
import java.nio.charset.StandardCharsets
import java.nio.file.Paths;

import scala.concurrent.{ExecutionContextExecutor, Future}

import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.FileIO
import akka.stream.ActorMaterializer

import akka.actor.ActorSystem

import com.typesafe.config.ConfigFactory


import akka.stream.scaladsl.{ Sink, Source }
import akka.Done

import org.json4s.native.Serialization.{read, write}
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.JsonMethods._




object Main extends App {


	implicit val actorSystem = ActorSystem()
	
	implicit val flowMaterializer = ActorMaterializer()

  	implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher

	implicit val formats = DefaultFormats
		
	val topicName : String = "test_topic"

	  val config = ConfigFactory.load("application.conf")
	  val producerConfig = config.getConfig("akka.kafka.producer")
	  println(producerConfig)
	  val producerSettings = ProducerSettings(producerConfig, new StringSerializer, new StringSerializer).withBootstrapServers("kafka:29092")


	
	val future: Future[Done] = FileIO.fromPath(Paths.get("/usr/src/app/KafkaElasticSink/src/resources/cfpb_complaints_cut.csv"))
							.via(CsvParsing.lineScanner())
							.via(CsvToMap.toMap())
							.map( _.mapValues(_.utf8String) )
							.map(elem => {  var elemUpd = elem
											if (elemUpd("location") == "") {
												
												elemUpd = elemUpd-("location") // remove from map!
											}
											else { println(elemUpd("location"))}
											elemUpd
											}) 

							.map((elem : Map[String, String]) => { /*println(elem); */( elem("complaint_id"), write(elem) ) })
							//.runForeach(println)
							.map(elem => { println(elem._2); new ProducerRecord[String, String]("test_topic",  elem._1, elem._2) }) // provide key value using complaint_id
							.runWith(Producer.plainSink(producerSettings))
							
  						

  	future.onComplete { _ =>
    		println("Done!")
    		actorSystem.terminate()
  	}
  	

}