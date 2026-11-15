package com.queuecare.config;

import com.queuecare.entity.Category;
import com.queuecare.entity.User;
import com.queuecare.enums.Role;
import com.queuecare.repository.CategoryRepository;
import com.queuecare.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Akun dan kategori awal supaya halaman per role bisa langsung diuji.
 * Password sengaja sederhana karena ini akun demo lokal.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seed("Admin QueueCare", "admin@queuecare.id", "admin123", Role.ADMIN);
        seed("Owner Demo", "owner@queuecare.id", "owner123", Role.OWNER);
        seed("Owner Dua", "owner2@queuecare.id", "owner123", Role.OWNER);
        seed("Customer Demo", "customer@queuecare.id", "customer123", Role.CUSTOMER);

        List.of("Barbershop", "Salon", "Servis Laptop", "Klinik Kecantikan")
                .forEach(this::seedKategori);
    }

    private void seed(String name, String email, String password, Role role) {
        if (userRepository.existsByEmail(email)) {
            return;
        }
        userRepository.save(new User(name, email, passwordEncoder.encode(password), role));
    }

    private void seedKategori(String name) {
        if (categoryRepository.existsByName(name)) {
            return;
        }
        categoryRepository.save(new Category(name));
    }
}
