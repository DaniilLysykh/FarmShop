package com.farm.marketplace.controller;

import com.farm.marketplace.payload.request.ProductRequest;
import com.farm.marketplace.payload.response.MessageResponse;
import com.farm.marketplace.payload.response.ProductResponse;
import com.farm.marketplace.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Добавить товар (только фермер)
    @PostMapping
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request,
                                                         Authentication authentication) {
        ProductResponse response = productService.createProduct(request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    // Получить свои товары (только фермер)
    @GetMapping("/my")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<List<ProductResponse>> getMyProducts(Authentication authentication) {
        List<ProductResponse> products = productService.getMyProducts(authentication.getName());
        return ResponseEntity.ok(products);
    }

    // Обновить свой товар (только фермер)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                         @Valid @RequestBody ProductRequest request,
                                                         Authentication authentication) {
        ProductResponse response = productService.updateProduct(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    // Удалить свой товар (только фермер)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<MessageResponse> deleteProduct(@PathVariable Long id,
                                                         Authentication authentication) {
        productService.deleteProduct(id, authentication.getName());
        return ResponseEntity.ok(new MessageResponse("Товар успешно удален"));
    }

    @PostMapping("/{id}/image")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<ProductResponse> uploadProductImage(@PathVariable Long id,
                                                              @RequestParam("file") MultipartFile file,
                                                              Authentication authentication) {
        ProductResponse response = productService.uploadProductImage(id, file, authentication.getName());
        return ResponseEntity.ok(response);
    }
}