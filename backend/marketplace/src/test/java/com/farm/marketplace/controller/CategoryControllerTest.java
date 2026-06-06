package com.farm.marketplace.controller;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryControllerTest {

    private final CategoryController categoryController = new CategoryController();

    @Test
    void getCategories_returnsAllEnumValues() {
        var response = categoryController.getCategories();

        assertEquals(200, response.getStatusCode().value());
        List<String> categories = response.getBody();
        assertNotNull(categories);
        assertEquals(6, categories.size());
        assertTrue(categories.contains("MILK"));
        assertTrue(categories.contains("BREAD"));
    }
}
