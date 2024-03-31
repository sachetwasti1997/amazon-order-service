package com.sachet.amazonorderservice.service;

import com.sachet.amazonorderservice.model.Item;
import com.sachet.amazonorderservice.model.Order;
import com.sachet.amazonorderservice.model.OrderStatus;
import com.sachet.amazonorderservice.publisher.OrderCreatedEventPublisher;
import com.sachet.amazonorderservice.repository.ItemRepository;
import com.sachet.amazonorderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderCreatedEventPublisher orderCreatedEventPublisher;

    public OrderService(OrderRepository orderRepository,
                        ItemRepository itemRepository,
                        OrderCreatedEventPublisher orderCreatedEventPublisher) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.orderCreatedEventPublisher = orderCreatedEventPublisher;
    }

    public List<Order> getUserOrder(String userEmail) {
        return orderRepository.findByUserEmailOrderByExpiresAtDesc(userEmail);
    }

    public Order createOrder(Order order) throws Exception {
        String itemId = order.getItemId();

        //Find if the item has a stock
        Optional<Item> item = itemRepository.findByItemId(itemId);
        if (item.isEmpty()) {
            LOGGER.error("Cannot create order as item is not present {}", itemId);
            throw new Exception("Cannot place the order as Item is not present!");
        }

        Item savedItem = item.get();
        LOGGER.info("Got the item {}", itemId);

        if (savedItem.getTotalQuantity() < order.getQuantity()) {
            LOGGER.error("Order quantity more {}", itemId);
            throw new Exception("Cannot place the order as order quantity is more!");
        }

        LOGGER.info("Set the amount left {} {}", itemId, order.getQuantity());
        savedItem.setTotalQuantity(savedItem.getTotalQuantity() - order.getQuantity());

        //set the expiration date
        LOGGER.info("Setting the expire Date {}", itemId);
        Date expireDate = new Date();
        expireDate.setTime(expireDate.getTime() + (1000 * 60));
        order.setExpiresAt(expireDate);

        //set the order status
        LOGGER.info("Set the order status {}", itemId);
        order.setStatus(OrderStatus.PENDING_PAYMENT.name());

        //save the order to the database
        LOGGER.info("Saving the Order {}", itemId);
        orderRepository.save(order);

        LOGGER.info("Order Created {}", order.getId());
        //publish the order created event
        orderCreatedEventPublisher.sendOrderCreatedEvent(order);

        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
