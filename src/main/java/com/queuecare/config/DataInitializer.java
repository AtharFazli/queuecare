package com.queuecare.config;

import com.queuecare.entity.User;
import com.queuecare.enums.Role;
import com.queuecare.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Akun awal biar halaman /admin, /owner, dan /customer bisa langsung diuji (Fase 1).
 * Password sengaja sederhana karena ini akun demo lokal.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seed("Admin QueueCare", "admin@queuecare.id", "admin123", Role.ADMIN);
        seed("Owner Demo", "owner@queuecare.id", "owner123", Role.OWNER);
        seed("Customer Demo", "customer@queuecare.id", "customer123", Role.CUSTOMER);
    }

    private void seed(String name, String email, String password, Role role) {
        if (userRepository.existsByEmail(email)) {
            return;
        }
        userRepository.save(new User(name, email, passwordEncoder.encode(password), role));
    }
}
