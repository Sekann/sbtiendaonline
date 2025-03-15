package com.tienda.app.controllers;

import com.tienda.app.dtos.auth.ProductRequest;
import com.tienda.app.enums.RoleName;
import com.tienda.app.models.Product;
import com.tienda.app.models.User;
import com.tienda.app.services.ProductService;
import com.tienda.app.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
        List<Product> products = productService.getProductsByUserId(userId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Product>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest productRequest, @RequestParam Long userId) {
        try {
            Optional<User> userOptional = this.userService.getUserById(userId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }
            User user = userOptional.get();
            RoleName roleName = user.getRole().getRoleName();
            // Validar si el usuario tiene rol de ADMIN o SELLER
            if (roleName != RoleName.ADMIN && roleName != RoleName.SELLER) {
                return ResponseEntity.status(403).body("No tienes permisos para crear productos.");
            }
            Product newProduct = productService.createProduct(productRequest, user);
            return ResponseEntity.status(201).body(newProduct);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error al crear el producto: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id, @RequestParam Long userId) {
        Optional<User> userOptional = userService.getUserById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
        }
        User user = userOptional.get();
        RoleName roleName = user.getRole().getRoleName();
        if (roleName != RoleName.ADMIN && roleName != RoleName.SELLER) {
            return ResponseEntity.status(403).body(Map.of("error", "No tienes permisos para eliminar productos."));
        }
        Optional<Product> productOptional = productService.getProductById(id);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Producto no encontrado"));
        }
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "Producto eliminado correctamente."));
    }
}
