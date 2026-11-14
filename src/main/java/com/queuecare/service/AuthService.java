package com.queuecare.service;

import com.queuecare.dto.RegisterRequest;
import com.queuecare.entity.User;
import com.queuecare.enums.Role;
import com.queuecare.exception.EmailAlreadyExistsException;
import com.queuecare.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email " + request.getEmail() + " sudah terdaftar. Pakai email lain atau masuk.");
        }
        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Role.CUSTOMER);
        return userRepository.save(user);
    }
}
