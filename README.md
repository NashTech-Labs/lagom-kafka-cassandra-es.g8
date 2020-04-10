# lagom-kafka-cassandra-es

In this template the data passes from one kafka topic to another, also the data passes to elasticsearch from kafka and persisted using Cassandra.
To run this template you need following in your system :
- Kafka installed in your system.If you don't have kafka in your system download it from here :
     https://kafka.apache.org/downloads
- Cassandra installed in your system. If you don't have cassandra you can download it from here :
     http://cassandra.apache.org/download/
- ElasticSearch installed in your system. If you don't have elasticsearch you can download it from here :
     https://www.elastic.co/downloads/elasticsearch
     
To run the template first you need to start Kafka, Cassandra and Elasticsearch. For that do the following:
- For Kafka, change the directory to the main kafka directory and then
    1. run the zookeeper
    ``bin/zookeeper-server-start.sh config/zookeeper.properties``
    2. run the kafka server
    ``bin/kafka-server-start.sh config/server.properties``
    3. create two topics named prod and productInfo
    ``bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic prod``
    4. create a producer that will populate the topic prod
    ``bin/kafka-console-producer.sh --broker-list localhost:9092 --topic prod``
    
    The data to be provided to producer will be in json form. It could be : ``{"id":"1","name":"Pens","quantity":10}``
- For Cassandra, open a new terminal and change directory to Cassandra main directory and run : ``./bin/cassandra -f``
- For Elasticsearch , open a new terminal and change directory to main directory of elasticsearch and run : ``bin/elasticsearch``

Now do the following:
- clone the repository
``git clone https://github.com/knoldus/lagom-kafka-cassandra-es.git``
- change directory
``cd lagom-kafka-cassandra-es``
- run the following commands one after the other

    ``sbt``
   
    ``clean``
    
    ``compile``
   
    ``runAll``