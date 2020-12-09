package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CartControllerTest {
    private CartController cartController;
    private final ItemRepository itemRepository = mock(ItemRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    public void setUp() {
        cartController = new CartController(userRepository, cartRepository, itemRepository);
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("rajaul");
        user.setPassword("12345678");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);

        List<Item> items = new ArrayList<>();
        for (long i = 0L; i < 3L; i++) {
            Item item = new Item();
            item.setId(i);
            item.setDescription("Drone");
            item.setName("Drone");
            item.setPrice(new BigDecimal("999.99"));
            cart.addItem(item);
        }
        cart.setItems(items);
        user.setCart(cart);

        return user;
    }

    private Item createItem() {
        Item item = new Item();
        item.setId(1L);
        item.setDescription("Drone");
        item.setName("Drone");
        item.setPrice(new BigDecimal("999.99"));

        return item;
    }

    private ModifyCartRequest createModifyCartRequest() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(3);
        modifyCartRequest.setUsername("rajaul");

        return modifyCartRequest;
    }

    @Test
    public void validateAddToCart() {
        when(userRepository.findByUsername("rajaul")).thenReturn(createUser());
        when(itemRepository.findById(1L)).thenReturn(Optional.of(createItem()));

        ResponseEntity<Cart> response = cartController.addTocart(createModifyCartRequest());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals("rajaul", cart.getUser().getUsername());
    }

    @Test
    public void validateRemoveFromCart() {
        when(userRepository.findByUsername("rajaul")).thenReturn(createUser());
        when(itemRepository.findById(1L)).thenReturn(Optional.of(createItem()));

        ResponseEntity<Cart> response = cartController.removeFromcart(createModifyCartRequest());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals("rajaul", cart.getUser().getUsername());
    }
}
