package com.sachet.amazonorderservice.repository;

import com.sachet.amazonorderservice.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserEmailOrderByExpiresAtDesc(String userEmail);
}
