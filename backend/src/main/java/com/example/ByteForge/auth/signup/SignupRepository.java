package com.example.ByteForge.auth.signup;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SignupRepository extends JpaRepository<SignupEntity, Long> {
    boolean existsByEmail(String email);
}
