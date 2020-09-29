FROM zenika/alpine-maven
RUN apk update \
	&& apk add ca-certificates wget \
	&& update-ca-certificates

#RUN mvn archetype:generate -B \
#    -DarchetypeGroupId=net.alchim31.maven -DarchetypeArtifactId=scala-archetype-simple -DarchetypeVersion=1.7 \
#    -DgroupId=com.scala -DartifactId=Scala -Dversion=0.1-SNAPSHOT -Dpackage=com.scala

RUN mkdir /usr/src/app/KafkaElasticSink