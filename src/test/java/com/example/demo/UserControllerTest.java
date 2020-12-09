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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserControllerTest {

    private UserController userController;
    private final UserRepository userRepo = mock(UserRepository.class);
    private final CartRepository cartRepo = mock(CartRepository.class);
    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @BeforeEach
    public void setUp() {
        userController = new UserController(userRepo, cartRepo, encoder);
    }

    @Test
    public void createUserHappyPath() {
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

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("rajaul");
        user.setPassword("12345678");

        return user;
    }

    @Test
    public void validateFindByUsername() {
        when(userRepo.findByUsername("rajaul")).thenReturn(createUser());

        final ResponseEntity<User> response = userController.findByUserName("rajaul");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User actualUser = response.getBody();
        assertNotNull(actualUser);
        assertEquals("rajaul", actualUser.getUsername());
    }

    @Test
    public void validateFindById() {
        User user = createUser();
        Optional<User> optUser = Optional.of(user);
        when(userRepo.findById(1L)).thenReturn(optUser);

        final ResponseEntity<User> response = userController.findById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User actualUser = response.getBody();
        assertNotNull(actualUser);
        assertEquals(user.getUsername(), actualUser.getUsername());
    }
}
