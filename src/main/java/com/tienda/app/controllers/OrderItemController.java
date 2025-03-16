package com.tienda.app.controllers;

import com.tienda.app.dtos.auth.OrderItemRequest;
import com.tienda.app.models.OrderItem;
import com.tienda.app.models.Product;
import com.tienda.app.services.OrderItemService;
import com.tienda.app.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order-items")
@CrossOrigin("*")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        List<OrderItem> orderItems = orderItemService.getAllOrderItems();
        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderItemById(@PathVariable Long id) {
        Optional<OrderItem> orderItem = orderItemService.getOrderItemById(id);

        if (orderItem.isPresent()) {
            return ResponseEntity.ok(orderItem.get());
        } else {
            return ResponseEntity.status(404).body("{\"message\": \"OrderItem no encontrado.\"}");
        }
    }

    @PostMapping
    public ResponseEntity<?> createOrderItem(@RequestBody OrderItemRequest orderItemRequest) {
        try {
            Optional<Product> productOpt = productService.getProductById(orderItemRequest.getProductId());

            if (productOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Producto no encontrado.");
            }

            Product product = productOpt.get();
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(orderItemRequest.getQuantity());

            // Calcular subtotal
            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(orderItemRequest.getQuantity()));

            orderItem.setSubtotal(subtotal.doubleValue());

            OrderItem savedOrderItem = orderItemService.saveOrderItem(orderItem);
            return ResponseEntity.status(201).body(savedOrderItem);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear el OrderItem: " + e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderItem(@PathVariable Long id) {
        Optional<OrderItem> orderItemOpt = orderItemService.getOrderItemById(id);

        if (orderItemOpt.isEmpty()) {
            return ResponseEntity.status(404).body("OrderItem no encontrado.");
        }

        orderItemService.deleteOrderItem(id);
        return ResponseEntity.ok().body("OrderItem eliminado correctamente.");
    }
}
