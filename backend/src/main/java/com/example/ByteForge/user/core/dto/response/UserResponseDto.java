package com.example.ByteForge.user.core.dto.response;

import com.example.ByteForge.user.core.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String name;
    private Long id;

    public UserResponseDto(UserEntity entity) {
        this.name = entity.getName();
        this.id = entity.getId();
    }
}