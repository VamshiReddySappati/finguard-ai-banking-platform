package com.finguard.transaction.config;
import org.apache.kafka.clients.admin.NewTopic;import org.springframework.context.annotation.*;import org.springframework.kafka.config.TopicBuilder;import org.springframework.web.client.RestClient;
@Configuration public class AppConfig {
 @Bean RestClient.Builder restClientBuilder(){return RestClient.builder();}
 @Bean NewTopic initiated(){return TopicBuilder.name("transaction.initiated.v1").partitions(3).replicas(1).build();}
 @Bean NewTopic decisions(){return TopicBuilder.name("fraud.decision.v1").partitions(3).replicas(1).build();}
 @Bean NewTopic completed(){return TopicBuilder.name("transaction.completed.v1").partitions(3).replicas(1).build();}
 @Bean NewTopic failed(){return TopicBuilder.name("transaction.failed.v1").partitions(3).replicas(1).build();}
}
