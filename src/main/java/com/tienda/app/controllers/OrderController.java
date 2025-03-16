package com.tienda.app.controllers;

import com.tienda.app.dtos.auth.OrderRequest;
import com.tienda.app.models.Order;
import com.tienda.app.models.OrderItem;
import com.tienda.app.models.User;
import com.tienda.app.services.OrderService;
import com.tienda.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
@CrossOrigin("*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        Optional<Order> orderOpt = orderService.getOrderById(id);

        if (orderOpt.isPresent()) {
            return ResponseEntity.ok(orderOpt.get());
        }

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "Order no encontrado.");

        return ResponseEntity.status(404).body(errorResponse);
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            Optional<User> userOpt = userService.getUserById(orderRequest.getUserId());

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuario no encontrado.");
            }

            User user = userOpt.get();
            Order order = new Order();
            order.setUser(user);
            order.setCreatedAt(LocalDateTime.now());

            BigDecimal total = orderRequest.getOrderItems().stream()
                    .map(item -> BigDecimal.valueOf(item.getSubtotal()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            order.setTotal(total.doubleValue());

            Order savedOrder = orderService.createOrder(order);
            return ResponseEntity.status(201).body(savedOrder);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear la orden: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        Optional<Order> orderOpt = orderService.getOrderById(id);

        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Orden no encontrada.");
        }

        orderService.deleteOrder(id);
        return ResponseEntity.ok().body("Orden eliminada correctamente.");
    }
}
