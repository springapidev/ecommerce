package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ItemControllerTest {

    private ItemController itemController;
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @BeforeEach
    public void setUp() {
        itemController = new ItemController(itemRepository);
    }

    private Item createNewItem() {
        Item item = new Item();

        item.setId(1L);
        item.setName("Drone");
        item.setDescription("Drone");
        item.setPrice(new BigDecimal("999.99"));

        return item;
    }

    @Test
    public void validateGetItemById() {
        Item item = createNewItem();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        final ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item item1 = response.getBody();
        assertNotNull(item1);
        assertEquals("Drone", item1.getName());
    }
    @Test
    public void checkGetItems() {
        Item item = createNewItem();
        List<Item> items = new ArrayList<>(Arrays.asList(item, item, item));
        when(itemRepository.findAll()).thenReturn(items);
        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> itemList = response.getBody();
        assertNotNull(itemList);
        assertEquals(item.getPrice(), itemList.get(1).getPrice());
    }
    @Test
    public void checkGetItemsByName() {
        Item item = createNewItem();
        List<Item> items = new ArrayList<>(Arrays.asList(item, item, item));
        when(itemRepository.findByName("Drone")).thenReturn(items);
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("Drone");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> itemList = response.getBody();
        assertNotNull(itemList);
        assertEquals("Drone", itemList.get(0).getName());
    }


}