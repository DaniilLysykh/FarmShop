package com.farm.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farm.marketplace.config.MethodSecurityTestConfig;
import com.farm.marketplace.exception.GlobalExceptionHandler;
import com.farm.marketplace.model.Category;
import com.farm.marketplace.payload.request.ProductRequest;
import com.farm.marketplace.payload.response.ProductResponse;
import com.farm.marketplace.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, MethodSecurityTestConfig.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private final ProductResponse sampleProduct = ProductResponse.builder()
            .id(1L)
            .name("Milk")
            .description("Fresh")
            .price(new BigDecimal("120.00"))
            .unit("l")
            .stock(10)
            .category(Category.MILK)
            .farmerId(2L)
            .imageUrl("/uploads/milk.jpg")
            .averageRating(4.5)
            .reviewCount(3L)
            .build();

    @Test
    void getPublicProducts_returnsPage() throws Exception {
        when(productService.getPublicProducts(isNull(), isNull(), isNull(), isNull(), isNull(), any()))
                .thenReturn(new PageImpl<>(List.of(sampleProduct)));

        mockMvc.perform(get("/api/products/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Milk"));
    }

    @Test
    @WithMockUser(username = "farmer@test.com", roles = "FARMER")
    void createProduct_success() throws Exception {
        ProductRequest request = buildProductRequest();

        when(productService.createProduct(any(ProductRequest.class), eq("farmer@test.com")))
                .thenReturn(sampleProduct);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Milk"));
    }

    @Test
    @WithMockUser(username = "farmer@test.com", roles = "FARMER")
    void getMyProducts_success() throws Exception {
        when(productService.getMyProducts("farmer@test.com")).thenReturn(List.of(sampleProduct));

        mockMvc.perform(get("/api/products/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(username = "farmer@test.com", roles = "FARMER")
    void updateProduct_success() throws Exception {
        ProductRequest request = buildProductRequest();

        when(productService.updateProduct(eq(1L), any(ProductRequest.class), eq("farmer@test.com")))
                .thenReturn(sampleProduct);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "farmer@test.com", roles = "FARMER")
    void deleteProduct_success() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Товар успешно удален"));

        verify(productService).deleteProduct(1L, "farmer@test.com");
    }

    @Test
    @WithMockUser(username = "farmer@test.com", roles = "FARMER")
    void uploadProductImage_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image-bytes".getBytes()
        );

        when(productService.uploadProductImage(eq(1L), any(), eq("farmer@test.com")))
                .thenReturn(sampleProduct);

        mockMvc.perform(multipart("/api/products/1/image").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").value("/uploads/milk.jpg"));
    }

    @Test
    @WithMockUser(username = "buyer@test.com", roles = "CUSTOMER")
    void createProduct_asCustomer_returnsForbidden() throws Exception {
        ProductRequest request = buildProductRequest();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    private ProductRequest buildProductRequest() {
        ProductRequest request = new ProductRequest();
        request.setName("Milk");
        request.setDescription("Fresh");
        request.setPrice(new BigDecimal("120.00"));
        request.setUnit("l");
        request.setStock(10);
        request.setCategory("MILK");
        return request;
    }
}
