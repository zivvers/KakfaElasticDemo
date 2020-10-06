# KakfaElasticDemo


## Running Demo

  1. Build & run docker images

  ```
  $ docker-compose build
  $ docker-compose up
  ```

  2. Create topic in Kafka

  ```
  docker exec -it kafkaelasticsink_kafka_1 kafka-topics --zookeeper zookeeper:2181 --create --topic test_topic --partitions 1 --replication-factor 1
  ```

  3. Create index in Elasticsearch (notice analyzers and strict mapping!)
  ```
  curl -X PUT "http://localhost:9200/test_topic?pretty" -H 'Content-Type: application/json' -d'
    {   "settings": {
              "number_of_shards": 3,
              "analysis": {
                  "filter": {
                      "english_stemmer": {
                          "name": "porter2", 
                          "type": "stemmer"
                      },
                      "english_stop": {
                          "type": "stop",
                          "stopwords":  "_english_"
                      }
                  },
                  "char_filter": {
                      "strip_server": {
                          "pattern": "\\\\\\\\",
                          "type": "pattern_replace",
                          "replacement": ""
                      },
                      "win_to_path": {
                          "pattern": "\\\\",
                          "type": "pattern_replace",
                          "replacement": "/"
                      }
                  },
                  "analyzer": {
                      "tag_words_fuzzy": {
                          "filter": ["english_stemmer"],
                          "char_filter": ["icu_normalizer"],
                          "tokenizer": "icu_tokenizer"
                      },

                      "lower_case": {
                          "filter": ["lowercase"],
                          "tokenizer": "keyword"
                      },

                      "tag_words": {
                          "filter": ["lowercase"],
                          "char_filter": ["icu_normalizer"],
                          "tokenizer": "icu_tokenizer"
                      },

                      "tag_words_remove_stop": {
                          "filter": ["lowercase", "english_stop"],
                          "char_filter": ["icu_normalizer"],
                          "tokenizer": "icu_tokenizer"
                      },
                          
                      "tag_words_fuzzy_stop": {
                          "filter": ["lowercase", "english_stop", "english_stemmer"],
                          "char_filter": ["icu_normalizer"],
                          "tokenizer": "icu_tokenizer"
                      }

                  },
                  "tokenizer": {
                      "word_split": {"pattern" : ".|,| ", "type" : "simple_pattern_split"}
                  }
              },
              "number_of_replicas": "1"
        },
        "mappings": {
            "dynamic": "strict",
            "properties": {
                "date_received": {
                    "type": "date"
                },
                "product" : {
                    "type": "keyword"
                },
                "sub_product" : {
                    "type": "keyword"
                },
                "issue": {
                    "type": "keyword"
                },
                "sub_issue": {
                    "type": "keyword"
                },
                "complaint_what_happened": {
                    "type": "text",
                    "store": true,
                    "fielddata": true,
                    "fields": {
                        "keyword": {
                            "type": "keyword"
                        },
                        "words": {
                            "type": "text",
                            "term_vector": "with_positions_offsets",
                            "analyzer": "tag_words"
                        },
                        "words_fuzzy": {
                            "type": "text",
                            "term_vector": "with_positions_offsets",
                            "analyzer": "tag_words_fuzzy"
                        },
                        "words_remove_stop": {
                            "type": "text",
                            "term_vector": "with_positions_offsets",
                            "analyzer": "tag_words_remove_stop"
                        },
                        "words_fuzzy_remove_stop": {
                            "type": "text",
                            "term_vector": "with_positions_offsets",
                            "analyzer": "tag_words_fuzzy_stop"
                        }
                    }
                },
                "company_public_response": {
                    "type": "text",
                    "store": true,
                    "fielddata": true,
                    "fields": {
                        "keyword": {
                            "type": "keyword"
                        },
                        "words": {
                            "type": "text",
                            "term_vector": "with_positions_offsets",
                            "analyzer": "tag_words"
                        },
                        "words_fuzzy": {
                            "type": "text",
                            "term_vector": "with_positions_offsets",
                            "analyzer": "tag_words_fuzzy"
                        },
                        "words_remove_stop": {
                            "type": "text",
                            "term_vector": "with_positions_offsets",
                            "analyzer": "tag_words_remove_stop"
                        },
                        "words_fuzzy_remove_stop": {
                            "type": "text",
                            "term_vector": "with_positions_offsets",
                            "analyzer": "tag_words_fuzzy_stop"
                        }
                    }
                },
                "company": {
                    "type": "keyword"
                },
                "state": {
                    "type": "keyword"
                },
                "zip_code": {
                    "type": "keyword"
                },
                "location": {
                    "type": "geo_point"
                },
                "tags": {
                    "type" : "keyword"
                },
                "consumer_consent_provided": {
                        "type": "keyword"
                },
                "submitted_via": {
                        "type" : "keyword"
                },
                "date_sent_to_company": {
                        "type" : "date"
                },
                "company_response": {
                        "type" : "keyword"
                },
                "timely" : {
                        "type" : "keyword"
                },
                "consumer_disputed" : {
                        "type" : "keyword"
                },
                "complaint_id" : {
                  "type" : "keyword"
                }

            }     
          }
      }
  '
  ```

  4. Configure Connector

  ```
  curl -X POST http://localhost:8083/connectors/ -H 'Content-Type: application/json' -d '{ "name": "elasticsearch-sink","config": { "connector.class": "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector", "tasks.max": "1", "topics": "test_topic", "key.ignore": "false", "schema.ignore": "true","connection.url": "http://elasticsearch:9200", "type.name": "", "name": "elasticsearch-sink", "value.converter.schemas.enable": "false", "key.converter.schemas.enable":"false" } }'
  ```

  5. Examine data within Kafka

  ```
  docker exec -it kafkaelasticsink_kafka_1 kafka-console-consumer --topic test_topic --bootstrap-server kafka:9092 \
   --property print.key=true \
   --property key.separator="-" \
   --partition 0 \
   --from-beginning
  --offset 10
  ```

  6. See data in Elasticsearch 
  ```
  curl -X GET "localhost:9200/test_topic/_search?pretty"
  ```

## Other Useful Commands

  1. ~Probably don't do this to a production system~ Purging data from Kafka (but not Elasticsearch) requires updating the retention period to something very short, and then changing back, e.g.
  ```
  docker exec -it kafka kafka-configs --bootstrap-server kafka:29092 --entity-type topics --alter --entity-name test_topic --add-config retention.ms=1000
  ```
  ...wait a few seconds (reverting to a week retention time)...
  ```
  docker exec -it kafka kafka-configs --bootstrap-server kafka:29092 --entity-type topics --alter --entity-name test_topic --add-config retention.ms=604800000
  ```

  2. Delete all documents in an index
  ```
  curl -X POST "localhost:9200/test_topic/_delete_by_query?pretty" -H 'Content-Type: application/json' -d'
  {
    "query": {
      "match_all": {
      }
    }
  }'
  ```