package com.tienda.app.controllers;

import com.tienda.app.dtos.auth.ProductRequest;
import com.tienda.app.models.Product;
import com.tienda.app.models.User;
import com.tienda.app.services.ProductService;
import com.tienda.app.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@CrossOrigin("*")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(@RequestParam Long userId) {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Product>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest productRequest, @RequestParam Long userId) {
        try {
            Optional<User> user = this.userService.getUserById(userId);
            if (user.isEmpty()) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }
            Product newProduct = productService.createProduct(productRequest, user.get());
            return ResponseEntity.status(201).body(newProduct);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error al crear el producto: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
