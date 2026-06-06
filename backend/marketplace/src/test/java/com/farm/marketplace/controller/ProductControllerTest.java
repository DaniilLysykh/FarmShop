package com.farm.marketplace.controller;

import com.farm.marketplace.model.Category;
import com.farm.marketplace.payload.request.ProductRequest;
import com.farm.marketplace.payload.response.MessageResponse;
import com.farm.marketplace.payload.response.ProductResponse;
import com.farm.marketplace.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock private ProductService productService;
    @InjectMocks private ProductController productController;

    @Test
    void getPublicProducts_returnsPage() {
        ProductResponse product = ProductResponse.builder().id(1L).name("Milk")
                .price(BigDecimal.TEN).category(Category.MILK).build();
        when(productService.getPublicProducts(isNull(), isNull(), isNull(), isNull(), isNull(), any()))
                .thenReturn(new PageImpl<>(List.of(product)));

        var response = productController.getPublicProducts(
                null, null, null, null, null, 0, 10, "id", "asc");

        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void deleteProduct_returnsMessage() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("farmer@test.com");

        var response = productController.deleteProduct(1L, auth);

        verify(productService).deleteProduct(1L, "farmer@test.com");
        assertEquals("Товар успешно удален", ((MessageResponse) response.getBody()).getMessage());
    }
}
