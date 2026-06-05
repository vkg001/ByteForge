package com.example.ByteForge.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    String name;

    public UserDto(UserEntity entity) {
        this.name = entity.getName();
    }
}
