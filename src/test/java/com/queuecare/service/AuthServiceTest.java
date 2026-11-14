package com.queuecare.service;

import com.queuecare.dto.RegisterRequest;
import com.queuecare.entity.User;
import com.queuecare.enums.Role;
import com.queuecare.exception.EmailAlreadyExistsException;
import com.queuecare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, new BCryptPasswordEncoder());
    }

    private RegisterRequest request(String email) {
        RegisterRequest request = new RegisterRequest();
        request.setName("Budi");
        request.setEmail(email);
        request.setPassword("password123");
        request.setConfirmPassword("password123");
        return request;
    }

    @Test
    void registerMenyimpanHashBukanPasswordAsli() {
        when(userRepository.existsByEmail("budi@mail.id")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = authService.register(request("budi@mail.id"));

        assertNotEquals("password123", saved.getPasswordHash());
        assertTrue(saved.getPasswordHash().startsWith("$2"), "hash BCrypt diawali $2");
        assertTrue(new BCryptPasswordEncoder().matches("password123", saved.getPasswordHash()));
        assertEquals(Role.CUSTOMER, saved.getRole());
    }

    @Test
    void registerEmailDuplikatDitolak() {
        when(userRepository.existsByEmail("budi@mail.id")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> authService.register(request("budi@mail.id")));
    }
}
