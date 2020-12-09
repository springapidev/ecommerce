package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OrderControllerTest {

    private OrderController orderController;
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    public void setUp() {
        orderController = new OrderController(userRepository, orderRepository);
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("rajaul");
        user.setPassword("12345678");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setTotal(new BigDecimal("8000.97"));

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

    @Test
    public void validateSubmitOrder() {
        when(userRepository.findByUsername("rajaul")).thenReturn(createUser());

        final ResponseEntity<UserOrder> response = orderController.submit("rajaul");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder orders = response.getBody();
        assertNotNull(orders);
        assertEquals("rajaul", orders.getUser().getUsername());
    }

    private List<UserOrder> createUserOrders() {
        List<UserOrder> userOrders = new ArrayList<>();
        for (long i = 0L; i < 3L; i++) {
            UserOrder order = new UserOrder();
            order.setId(i);
            order.setUser(createUser());

            List<Item> items = new ArrayList<>();
            for (long j = 0L; j < 3L; j++) {
                Item item = new Item();
                item.setId(j);
                item.setDescription("Drone");
                item.setName("Drone");
                item.setPrice(new BigDecimal("999.99"));
                items.add(item);
            }
            order.setItems(items);

            order.setTotal(new BigDecimal("8000.97"));

            userOrders.add(order);
        }
        return userOrders;
    }

    @Test
    public void validateGetOrdersForUser() {
        User user = createUser();
        when(userRepository.findByUsername("rajaul")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(createUserOrders());
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("rajaul");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);
        assertEquals(new BigDecimal("8000.97"), orders.get(1).getTotal());
    }
}