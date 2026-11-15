package com.queuecare.service;

import com.queuecare.dto.RegisterRequest;
import com.queuecare.entity.User;
import com.queuecare.enums.Role;
import com.queuecare.exception.AturanAkunException;
import com.queuecare.exception.EmailAlreadyExistsException;
import com.queuecare.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
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

    /** Ubah nama dan email akun milik sendiri (R2). Email tetap unik. */
    @Transactional
    public void ubahProfil(Long id, String nama, String email) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AccessDeniedException("Pengguna tidak dikenal."));
        if (userRepository.existsByEmailAndIdNot(email, id)) {
            throw new EmailAlreadyExistsException(
                    "Email " + email + " sudah dipakai pengguna lain. Pakai email lain.");
        }
        user.setName(nama);
        user.setEmail(email);
    }

    /** Ganti password akun milik sendiri (R2). Password lama harus cocok, baru tidak boleh sama. */
    @Transactional
    public void gantiPassword(Long id, String lama, String baru) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AccessDeniedException("Pengguna tidak dikenal."));
        if (!passwordEncoder.matches(lama, user.getPasswordHash())) {
            throw new AturanAkunException("Password lama tidak cocok.");
        }
        if (passwordEncoder.matches(baru, user.getPasswordHash())) {
            throw new AturanAkunException("Password baru tidak boleh sama dengan password lama.");
        }
        user.setPasswordHash(passwordEncoder.encode(baru));
    }
}
