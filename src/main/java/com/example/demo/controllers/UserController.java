package com.example.demo.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, CartRepository cartRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id, Authentication authentication) {
        Optional<User> user = null;
        try {
            authentication = SecurityContextHolder.getContext().getAuthentication();
            if (userRepository.findByUsername(authentication.getName()) == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            user = userRepository.findById(id);
            if(userRepository.findByUsername(authentication.getName()) != null && user.get().getUsername() == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (NullPointerException ne) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.of(user);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        LOGGER.info("Username is used to find a user is ", username);
        return user == null ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        LOGGER.info("Username is set with {}", createUserRequest.getUsername());
        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);
        if (createUserRequest.getPassword().length() < 8) {
            LOGGER.error("User Creation is failed: Password length has not met, Password length required at least 8 characters. So, Cannot create an user {}. ", createUserRequest.getUsername());
            return ResponseEntity.badRequest().build();
        } else if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            LOGGER.error("User Creation is failed: Password is not matched with ConfirmPassword. So, Cannot create an user {}. ", createUserRequest.getUsername());
            return ResponseEntity.badRequest().build();
        }
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        userRepository.save(user);
        LOGGER.info("User Creation is success: User {} created with requested username, cart, and encoded password", createUserRequest.getUsername());
        return ResponseEntity.ok(user);
    }

}
