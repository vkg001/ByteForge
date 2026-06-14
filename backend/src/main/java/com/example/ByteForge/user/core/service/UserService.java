package com.example.ByteForge.user.core.service;

import com.example.ByteForge.user.core.dto.response.UserResponseDto;
import com.example.ByteForge.user.core.entity.UserEntity;
import com.example.ByteForge.user.core.exception.UserNotFoundException;
import com.example.ByteForge.user.core.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UsersRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = repository.findByEmail(email);
        if (user != null) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .authorities(user.getUserRole().toString())
                    .build();
        }
        throw new UsernameNotFoundException(email);
    }

    public UserResponseDto getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UserNotFoundException("Invalid Session. Details not available");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (userDetails == null) throw new UserNotFoundException("Invalid Session");

        UserEntity userEntity = repository.findByEmail(userDetails.getUsername());
        return new UserResponseDto(userEntity);
    }

    public UserEntity getCurrentUserDetailsInEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UserNotFoundException("Invalid Session. Details not available");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (userDetails == null) throw new UserNotFoundException("Invalid Session");

        return repository.findByEmail(userDetails.getUsername());
    }

    public Optional<UserResponseDto> getUserDetailsById(Long id) {
        return repository.findById(id).map(UserResponseDto::new);
    }
}