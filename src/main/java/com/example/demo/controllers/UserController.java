package com.example.demo.controllers;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        LOGGER.info("Username is used to find a user is ", username);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
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
            LOGGER.error("User Creattion is failed: Password length has not met, Password length required at least 8 characters. So, Cannot create an user {}. ", createUserRequest.getUsername());
            return ResponseEntity.badRequest().build();
        } else if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            LOGGER.error("User Creattion is failed: Password is not matched with ConfirmPassword. So, Cannot create an user {}. ", createUserRequest.getUsername());
            return ResponseEntity.badRequest().build();
        }
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        userRepository.save(user);
        LOGGER.info("User Creattion is success: User {} created with requested username, cart, and encoded password", createUserRequest.getUsername());
        return ResponseEntity.ok(user);
    }

}
