FROM docker.elastic.co/kibana/kibana:7.3.2
RUN printf "# updated Docker Kibana config\nserver.name: kibana\nserver.host: \"0\"\nelasticsearch.url: http://es01:9200\nxpack.monitoring.ui.container.elasticsearch.enabled: true\nlogging.events.log: ['warning','error','fatal','info','reporting']" > /usr/share/kibana/config/kibana.yml
# put it here /usr/share/kibana/config/kibana.yml
