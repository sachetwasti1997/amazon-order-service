package com.sachet.amazonorderservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sachet.amazonorderservice.model.Order;
import com.sachet.amazonorderservice.model.OrderCreatedEventModal;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;


@Component
public class OrderCreatedEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCreatedEventPublisher.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public OrderCreatedEventPublisher(@Value("") String topic,
                                      KafkaTemplate<String, String> kafkaTemplate,
                                      ObjectMapper objectMapper) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<SendResult<String, String>>
    sendOrderCreatedEvent(Order order) throws JsonProcessingException {
        OrderCreatedEventModal modal = new OrderCreatedEventModal(
                order.getId(),
                order.getStatus(),
                order.getUserId(),
                order.getExpiresAt().toString(),
                order.getItemId(),
                order.getItemPrice()
        );
        String value = objectMapper.writeValueAsString(modal);
        LOGGER.info("Sending order created event {}", value);
        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>(topic, order.getId(), value);
        return kafkaTemplate
                .send(producerRecord)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        LOGGER.error("Error while publishing the event {}", throwable.getMessage());
                    }else {
                        LOGGER.info("Successfully published the event {}", result);
                    }
                });
    }
}
