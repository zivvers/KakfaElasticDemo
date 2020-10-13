FROM docker.elastic.co/elasticsearch/elasticsearch:7.3.2
RUN /usr/share/elasticsearch/bin/elasticsearch-plugin install analysis-icu
#RUN printf "cluster.name: \"docker-cluster\"\nnetwork.host: 0.0.0.0\nxpack.security.enabled: false" > /usr/share/elasticsearch/config/elasticsearch.yml

RUN bin/elasticsearch-certutil cert -out /usr/share/elasticsearch/config/elastic-certificates.p12 -pass ""

RUN printf "xpack.security.enabled: true\nxpack.security.transport.ssl.enabled: true\nxpack.security.transport.ssl.verification_mode: certificate\nxpack.security.transport.ssl.keystore.path: elastic-certificates.p12\nxpack.security.transport.ssl.truststore.path: elastic-certificates.p12" >> /usr/share/elasticsearch/config/elasticsearch.yml

RUN chown -R elasticsearch:elasticsearch /usr/share/elasticsearch/config