package com.farm.marketplace.controller;

import com.farm.marketplace.controller.support.ControllerTestSetup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class)
@ControllerTestSetup
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getCategories_returnsAllEnumValues() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0]").value("MILK"))
                .andExpect(jsonPath("$[5]").value("BREAD"));
    }
}
