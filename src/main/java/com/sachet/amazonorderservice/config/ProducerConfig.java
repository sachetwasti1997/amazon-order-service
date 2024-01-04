package com.sachet.amazonorderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class ProducerConfig {

    private String orderCreatedTopic;
    private String cancelOrderTopic;

    public ProducerConfig(@Value("${spring.kafka.ordercreatedtopic}") String orderCreatedTopic,
                          @Value("${spring.kafka.cancelordertopic}") String cancelOrderTopic) {
        this.orderCreatedTopic = orderCreatedTopic;
        this.cancelOrderTopic = cancelOrderTopic;
    }

    @Bean
    public NewTopic orderCreatedEvent() {
        return TopicBuilder
                .name(orderCreatedTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderCancelledTopic() {
        return TopicBuilder
                .name(cancelOrderTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
