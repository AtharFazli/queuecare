package com.queuecare.config;

import com.queuecare.entity.User;
import com.queuecare.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Mengirim nama pengguna ke navbar di semua halaman. */
@ControllerAdvice
public class NavbarAdvice {

    private final UserRepository userRepository;

    public NavbarAdvice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute("nama")
    public String nama(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return userRepository.findByEmail(authentication.getName())
                .map(User::getName)
                .orElse(null);
    }
}
