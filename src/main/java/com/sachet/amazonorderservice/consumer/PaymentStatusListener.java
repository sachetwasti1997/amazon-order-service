package com.sachet.amazonorderservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sachet.amazonorderservice.model.Order;
import com.sachet.amazonorderservice.model.PaymentStatus;
import com.sachet.amazonorderservice.repository.OrderRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Optional;

@Configuration
public class PaymentStatusListener implements AcknowledgingMessageListener<String, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentStatusListener.class);

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    public PaymentStatusListener(ObjectMapper objectMapper, OrderRepository orderRepository) {
        this.objectMapper = objectMapper;
        this.orderRepository = orderRepository;
    }

    @KafkaListener(
            topics = "${spring.kafka.paymentTopic}",
            containerFactory = "kafkaPaymentStatusListenerContainerFactory",
            groupId = "${spring.kafka.orderserviceconsumer.group-id}"
    )
    @Override
    public void onMessage(ConsumerRecord<String, String> data, Acknowledgment acknowledgment) {
        LOGGER.info("Consuming Payment Status {}", data);
        try {
            PaymentStatus paymentStatus = objectMapper.readValue(data.value(), PaymentStatus.class);
            Optional<Order> optionalOrder = orderRepository.findById(paymentStatus.getOrderId());
            if (optionalOrder.isEmpty()) {
                throw new Exception("Cannot find the order");
            }
            Order savedOrder = optionalOrder.get();
            savedOrder.setStatus(paymentStatus.getPaymentStatus());
            orderRepository.save(savedOrder);
            acknowledgment.acknowledge();
        }catch (JsonProcessingException ex) {
            LOGGER.error("Error while processing JSON {},",ex.getMessage());
        }catch (Exception ex) {
            LOGGER.error("Error {}", ex.getMessage());
        }
    }
}
