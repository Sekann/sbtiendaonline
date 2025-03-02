package com.tienda.app.controllers;

import com.tienda.app.dtos.auth.LoginRequest;
import com.tienda.app.dtos.auth.LoginResponse;
import com.tienda.app.dtos.auth.RegisterRequest;
import com.tienda.app.models.User;
import com.tienda.app.services.UserService;
import jakarta.persistence.Id;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/*
 *   200-> todo bien
 *   201 todo bien pero para update
 *   204 todo bien pero para borrar
 *   400 error de identificacion
 *   401 error de datos incorectos
 *   403 permiso denegado
 *   404 no se a encontrado
 *   500 error en el servidor
 */

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(this.userService.getAllUsers());
    }

    @GetMapping("/id")
    public ResponseEntity<Optional<User>> getUserById(Long id) {
        return ResponseEntity.ok(this.userService.getUserById(id));
    }

    @GetMapping("/username")
    public ResponseEntity<Optional<User>> findByUsername(String username) {
        return ResponseEntity.ok(this.userService.findByUsername(username));
    }

    @DeleteMapping("/id")
    public void deleteUserById(Long id) {
        if (userService.getUserById(id).isPresent()) {
            userService.deleteUserById(id);
        }
    }

    //htt://localhost:8080/api/users/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest credentials) {
        try {
            LoginResponse loginResponse = this.userService.login(credentials);
            return ResponseEntity.ok(loginResponse);
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            User user = this.userService.createUser(registerRequest);
            return ResponseEntity.ok(user);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

}
