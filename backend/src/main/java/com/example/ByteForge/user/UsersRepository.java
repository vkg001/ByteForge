package com.example.ByteForge.user;

import com.example.ByteForge.user.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
    UserEntity findByEmail(String email);
}
