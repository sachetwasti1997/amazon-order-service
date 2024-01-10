package com.sachet.amazonorderservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sachet.amazonorderservice.model.Order;
import com.sachet.amazonorderservice.model.OrderCancelledEventModal;
import com.sachet.amazonorderservice.model.OrderCreatedEventModal;
import com.sachet.amazonorderservice.repository.OrderRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderExpireConsumer implements AcknowledgingMessageListener<String, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderExpireConsumer.class);
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    public OrderExpireConsumer(OrderRepository orderRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @KafkaListener(
            topics = "${spring.kafka.expireordertopic}",
            groupId = "${spring.kafka.orderserviceconsumer.group-id}",
            containerFactory = "kafkaOrderExpiredListenerContainerFactory"
    )
    public void onMessage(ConsumerRecord<String, String> data, Acknowledgment acknowledgment) {
        LOGGER.info("Order ConsumerRecord {}", data);
        try {
            OrderCancelledEventModal order = objectMapper.readValue(data.value(), OrderCancelledEventModal.class);
            orderRepository.deleteById(order.getOrderId());
        } catch (JsonProcessingException ex) {
            LOGGER.error("Received error when reading item-created-event {}", ex.getMessage());
        }
    }
}
