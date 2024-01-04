package com.sachet.amazonorderservice.repository;

import com.sachet.amazonorderservice.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ItemRepository extends MongoRepository<Item, String> {
    Optional<Item> findByItemId(String itemId);
}
