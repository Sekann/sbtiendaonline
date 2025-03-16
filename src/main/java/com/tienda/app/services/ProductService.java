package com.tienda.app.services;

import com.tienda.app.dtos.auth.ProductRequest;
import com.tienda.app.models.Product;
import com.tienda.app.models.User;
import com.tienda.app.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    public List<Product> getProductsByUserId(Long userId) {
        return productRepository.findByUserId(userId);
    }
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(ProductRequest productRequest, User user) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setImageUrl(productRequest.getImageUrl());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setTax(productRequest.getTax());
        product.setCurrency(productRequest.getCurrency());
        product.setUser(user);
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}