package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserControllerTest {

    private UserController userController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @BeforeEach
    public void setUp() {
        userController = new UserController(userRepository, cartRepository, encoder);
    }

    @Test
    public void createUserData() {
        when(encoder.encode("12345678")).thenReturn("whoAmI");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("rajaul");
        request.setPassword("12345678");
        request.setConfirmPassword("12345678");
        final ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("rajaul", user.getUsername());
        assertEquals("whoAmI", user.getPassword());
    }

    private User createNewUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("rajaul");
        user.setPassword("12345678");
        return user;
    }


    @Test
    public void validateUserFindById() {
        User user = createNewUser();
        Optional<User> optUser = Optional.of(user);
        when(userRepository.findById(1L)).thenReturn(optUser);
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        final ResponseEntity<User> response = userController.findById(1L,authentication);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user1 = response.getBody();
        assertNotNull(user1);
        assertEquals(user.getUsername(), user1.getUsername());
    }
    @Test
    public void validateUserFindByUsername() {
        when(userRepository.findByUsername("rajaul")).thenReturn(createNewUser());
        final ResponseEntity<User> response = userController.findByUserName("rajaul");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user1 = response.getBody();
        assertNotNull(user1);
        assertEquals("rajaul", user1.getUsername());
    }

}
