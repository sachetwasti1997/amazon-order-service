package com.sachet.amazonorderservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sachet.amazonorderservice.model.Item;
import com.sachet.amazonorderservice.repository.ItemRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ItemCreatedConsumer implements AcknowledgingMessageListener<String, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemCreatedConsumer.class);
    private final ItemRepository itemRepository;
    private final ObjectMapper objectMapper;

    public ItemCreatedConsumer(ItemRepository itemRepository,
                               ObjectMapper objectMapper) {
        this.itemRepository = itemRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @KafkaListener(
            topics = {"item-created-event"},
            groupId = "${spring.kafka.itemcreatedlisteners.group-id}",
            containerFactory = "kafkaItemCreatedListenerContainerFactory"
    )
    public void onMessage(@NonNull ConsumerRecord<String, String> data,
                          Acknowledgment acknowledgment) {
        LOGGER.info("ConsumerRecord: {}", data);
        Item item;
        try {
            item = objectMapper.readValue(data.value(), Item.class);
            LOGGER.info("Item Received {}", item);
            itemRepository.save(item);
            assert acknowledgment != null;
            acknowledgment.acknowledge();
        }catch (JsonProcessingException ex) {
            LOGGER.error("Received error when reading item-created-event {}", ex.getMessage());
        }
    }
}
