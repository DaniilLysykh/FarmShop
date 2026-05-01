package com.farm.marketplace.service;

import com.farm.marketplace.exception.AppException;
import com.farm.marketplace.model.Category;
import com.farm.marketplace.model.Product;
import com.farm.marketplace.model.User;
import com.farm.marketplace.payload.request.ProductRequest;
import com.farm.marketplace.payload.response.ProductResponse;
import com.farm.marketplace.repository.ProductRepository;
import com.farm.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Вспомогательный метод для маппинга сущности в DTO
    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .unit(product.getUnit())
                .stock(product.getStock())
                .category(product.getCategory())
                .farmerId(product.getFarmer().getId())
                .imageUrl(product.getImageUrl())
                .build();
    }

    // Вспомогательный метод получения юзера из БД по email
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Пользователь не найден", HttpStatus.NOT_FOUND));
    }

    public ProductResponse createProduct(ProductRequest request, String email) {
        User farmer = getUserByEmail(email);

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .unit(request.getUnit())
                .stock(request.getStock())
                .category(Category.valueOf(request.getCategory().toUpperCase()))
                .farmer(farmer)
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    public List<ProductResponse> getMyProducts(String email) {
        User farmer = getUserByEmail(email);
        return productRepository.findAllByFarmerId(farmer.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse uploadProductImage(Long productId, MultipartFile file, String email) {
        User farmer = getUserByEmail(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException("Товар не найден", HttpStatus.NOT_FOUND));

        // Проверка безопасности: только владелец может менять фото
        if (!product.getFarmer().getId().equals(farmer.getId())) {
            throw new AppException("У вас нет прав на редактирование этого товара", HttpStatus.FORBIDDEN);
        }

        try {
            // Указываем путь к папке uploads в корне проекта
            Path uploadDir = Paths.get("uploads");

            // Если папки нет, создаем ее
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Генерируем уникальное имя файла (UUID + оригинальное имя)
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);

            // Копируем файл из запроса на жесткий диск
            Files.copy(file.getInputStream(), filePath);

            // Сохраняем относительный путь в базу данных
            product.setImageUrl("/uploads/" + fileName);

            return mapToResponse(productRepository.save(product));

        } catch (Exception e) {
            throw new AppException("Ошибка при сохранении файла: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ProductResponse updateProduct(Long productId, ProductRequest request, String email) {
        User farmer = getUserByEmail(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException("Товар не найден", HttpStatus.NOT_FOUND));

        // Важная проверка безопасности: принадлежит ли товар этому фермеру?
        if (!product.getFarmer().getId().equals(farmer.getId())) {
            throw new AppException("У вас нет прав на редактирование этого товара", HttpStatus.FORBIDDEN);
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setUnit(request.getUnit());
        product.setStock(request.getStock());
        product.setCategory(Category.valueOf(request.getCategory().toUpperCase()));

        return mapToResponse(productRepository.save(product));
    }

    public void deleteProduct(Long productId, String email) {
        User farmer = getUserByEmail(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException("Товар не найден", HttpStatus.NOT_FOUND));

        if (!product.getFarmer().getId().equals(farmer.getId())) {
            throw new AppException("У вас нет прав на удаление этого товара", HttpStatus.FORBIDDEN);
        }

        productRepository.delete(product);
    }
}