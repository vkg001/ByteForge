package com.example.ByteForge.auth.signup;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "register")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SignupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    SignupEntity(SignupDto userData) {
        this.name = userData.getName();
        this.email = userData.getEmail();
        this.password = userData.getPassword();
    }
}
