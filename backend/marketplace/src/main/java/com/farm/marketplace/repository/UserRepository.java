package com.farm.marketplace.repository;

import com.farm.marketplace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring сам напишет SQL-запрос: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // Проверка, существует ли пользователь (возвращает true/false)
    boolean existsByEmail(String email);
}