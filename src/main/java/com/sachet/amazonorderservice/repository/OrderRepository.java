package com.sachet.amazonorderservice.repository;

import com.sachet.amazonorderservice.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
}
