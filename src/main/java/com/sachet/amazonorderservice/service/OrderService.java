package com.sachet.amazonorderservice.service;

import com.sachet.amazonorderservice.model.Item;
import com.sachet.amazonorderservice.model.Order;
import com.sachet.amazonorderservice.model.OrderStatus;
import com.sachet.amazonorderservice.publisher.OrderCreatedEventPublisher;
import com.sachet.amazonorderservice.repository.ItemRepository;
import com.sachet.amazonorderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

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

    public Order createOrder(Order order) throws Exception {
        String itemId = order.getItemId();

        //Find if the item has a stock
        Optional<Item> item = itemRepository.findByItemId(itemId);
        if (item.isEmpty()) {
            throw new Exception("Cannot place the order as Item is not present!");
        }

        Item savedItem = item.get();

        if (savedItem.getTotalQuantity() < order.getQuantity()) {
            throw new Exception("Cannot place the order as order quantity is more!");
        }

        savedItem.setTotalQuantity(savedItem.getTotalQuantity() - order.getQuantity());

        //set the expiration date
        Date expireDate = new Date();
        expireDate.setTime(expireDate.getTime() + (1000 * 60));
        order.setExpiresAt(expireDate);

        //set the order status
        order.setStatus(OrderStatus.PENDING_PAYMENT.name());

        //save the order to the database
        orderRepository.save(order);

        //publish the order created event
        orderCreatedEventPublisher.sendOrderCreatedEvent(order);

        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
