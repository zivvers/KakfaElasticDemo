FROM docker.elastic.co/elasticsearch/elasticsearch:7.3.2
RUN /usr/share/elasticsearch/bin/elasticsearch-plugin install analysis-icu
#RUN printf "cluster.name: \"docker-cluster\"\nnetwork.host: 0.0.0.0\nxpack.security.enabled: false" > /usr/share/elasticsearch/config/elasticsearch.yml