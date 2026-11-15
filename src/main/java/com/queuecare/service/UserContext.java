package com.queuecare.service;

import com.queuecare.entity.User;
import com.queuecare.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Membaca pengguna yang sedang login. Aturan kepemilikan bisnis dicek di sini
 * supaya lembar tampilan tidak menentukan hak akses (KNF-05).
 */
@Component
public class UserContext {

    private final UserRepository userRepository;

    public UserContext(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Tidak ada sesi login.");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("Pengguna tidak dikenal."));
    }

    /** Menolak kalau pemilik data bukan pengguna yang sedang login. */
    public void pastikanPemilik(User owner) {
        if (!owner.getId().equals(currentUser().getId())) {
            throw new AccessDeniedException("Kamu tidak punya hak akses ke data ini.");
        }
    }
}
