package com.farm.marketplace.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users") // Имя таблицы во множественном числе - хорошая практика
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // В БД сохраняем не сырой пароль, а хэш (требование безопасности)
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Создает отдельную таблицу user_roles для хранения ролей пользователя
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING) // Храним названия ролей текстом, а не цифрами
    private Set<Role> roles;

    // Автоматически проставляет время при сохранении в БД
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}