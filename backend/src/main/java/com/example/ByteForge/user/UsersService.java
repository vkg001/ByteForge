package com.example.ByteForge.user;

import com.example.ByteForge.user.exceptions.UserNotFoundException;
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
public class UsersService  implements UserDetailsService {
    @Autowired
    private UsersRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = repository.findByEmail(email);
        if (user != null) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .authorities(user.getUserRole().toString()) // DO NOT LEAVE THIS EMPTY
                    .build();
        }
        throw new UsernameNotFoundException(email);
    }

    public UserDto getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null  ||  !authentication.isAuthenticated()  ||  "anonymousUser".equals(authentication.getPrincipal())) {
            throw  new UserNotFoundException("Invalid Session. Details not available");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (userDetails == null) throw  new UserNotFoundException("Invalid Session");
        String email = userDetails.getUsername();

        UserEntity userEntity = repository.findByEmail(email);
        userEntity.setPassword("");
        return new UserDto(userEntity);
    }

    public UserEntity getCurrentUserDetailsInEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null  ||  !authentication.isAuthenticated()  ||  "anonymousUser".equals(authentication.getPrincipal())) {
            throw  new UserNotFoundException("Invalid Session. Details not available");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (userDetails == null) throw  new UserNotFoundException("Invalid Session");
        String email = userDetails.getUsername();

        return repository.findByEmail(email);
    }

    public Optional<UserDto> getUserDetailsById(Long Id) {
        var user = repository.findById(Id);
        return user.map(entity -> Optional.of(new UserDto(entity))).orElse(null);
    }
}
