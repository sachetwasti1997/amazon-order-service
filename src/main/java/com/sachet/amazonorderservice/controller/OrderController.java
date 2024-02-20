package com.sachet.amazonorderservice.controller;

import com.sachet.amazonorderservice.model.Order;
import com.sachet.amazonorderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) throws Exception {
        LOGGER.info("Request received {}", order.getItemId());
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @GetMapping("/test/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
