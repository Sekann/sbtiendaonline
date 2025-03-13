package com.tienda.app.services;

import com.tienda.app.models.Order;
import com.tienda.app.repositories.OrderRepository;
import org.springframework.stereotype.Service;
<<<<<<< HEAD

import java.util.List;
=======
import java.util.List;
import java.util.Optional;
>>>>>>> f1b7057 (Primer commit)

@Service
public class OrderService {

<<<<<<< HEAD
    private OrderRepository orderRepository;

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

=======
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
>>>>>>> f1b7057 (Primer commit)
}
