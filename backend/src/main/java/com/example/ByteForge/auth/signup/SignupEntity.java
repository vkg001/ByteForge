package com.example.ByteForge.auth.signup;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "register")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class SignupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 2, max = 40, message = "Name should be between 2 to 40 characters")
    private String name;

    @Column(nullable = false, unique = true)
    @Size(min = 5, max = 40, message = "E-Mail ID should be between 5 to 40 characters")
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDateTime;


    SignupEntity(SignupDto userData) {
        this.name = userData.getName();
        this.email = userData.getEmail();
        this.password = userData.getPassword();
    }
}
