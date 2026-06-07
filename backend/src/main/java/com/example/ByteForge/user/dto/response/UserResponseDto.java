package com.example.ByteForge.user.dto.response;

import com.example.ByteForge.user.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    String name;

    public UserResponseDto(UserEntity entity) {
        this.name = entity.getName();
    }
}
