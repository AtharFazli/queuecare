package com.queuecare.service;

import com.queuecare.dto.AdminUserForm;
import com.queuecare.entity.Business;
import com.queuecare.entity.User;
import com.queuecare.enums.Role;
import com.queuecare.exception.AturanAkunException;
import com.queuecare.exception.EmailAlreadyExistsException;
import com.queuecare.exception.TidakDitemukanException;
import com.queuecare.repository.BusinessRepository;
import com.queuecare.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Pengelolaan akun oleh Admin (R12) beserta aturan keselamatannya (KNF-03). */
@Service
public class AdminUserService {

    private static final int UKURAN_HALAMAN = 20;

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(UserRepository userRepository,
                            BusinessRepository businessRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<User> daftar(String q, Role role, int halaman) {
        String kunci = (q == null || q.isBlank()) ? null : q.trim();
        return userRepository.cariUntukAdmin(kunci, role,
                PageRequest.of(Math.max(halaman - 1, 0), UKURAN_HALAMAN));
    }

    public User cari(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new TidakDitemukanException("Pengguna tidak ditemukan."));
    }

    @Transactional
    public User tambah(AdminUserForm form) {
        Role role = parseRole(form.getRole());
        pastikanEmailBebas(form.getEmail(), null);
        if (form.getPassword() == null || form.getPassword().length() < 8) {
            throw new AturanAkunException("Password awal minimal 8 karakter.");
        }
        return userRepository.save(
                new User(form.getName(), form.getEmail(), passwordEncoder.encode(form.getPassword()), role));
    }

    @Transactional
    public User ubah(Long id, AdminUserForm form, User admin) {
        User target = cari(id);
        Role roleBaru = parseRole(form.getRole());
        pastikanEmailBebas(form.getEmail(), target.getId());
        pastikanBukanAdminSendiri(target, admin, roleBaru);
        pastikanAdminTerakhirBolehDiubah(target, roleBaru);
        pastikanOwnerBisnisBolehDiubah(target, roleBaru);

        target.setName(form.getName());
        target.setEmail(form.getEmail());
        target.setRole(roleBaru);
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            if (form.getPassword().length() < 8) {
                throw new AturanAkunException("Password baru minimal 8 karakter.");
            }
            target.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        }
        return target;
    }

    @Transactional
    public User ubahStatus(Long id, boolean aktif, User admin) {
        User target = cari(id);
        if (!aktif) {
            if (target.getId().equals(admin.getId())) {
                throw new AturanAkunException("Kamu tidak bisa menonaktifkan akun sendiri.");
            }
            if (target.getRole() == Role.ADMIN && target.isActive()
                    && userRepository.countByRoleAndActiveTrue(Role.ADMIN) <= 1) {
                throw new AturanAkunException("Admin aktif terakhir tidak bisa dinonaktifkan.");
            }
        }
        target.setActive(aktif);
        return target;
    }

    private Role parseRole(String nilai) {
        try {
            return Role.valueOf(nilai);
        } catch (RuntimeException ex) {
            throw new AturanAkunException("Role tidak dikenal.");
        }
    }

    private void pastikanEmailBebas(String email, Long idPengguna) {
        boolean dipakai = idPengguna == null
                ? userRepository.existsByEmail(email)
                : userRepository.existsByEmailAndIdNot(email, idPengguna);
        if (dipakai) {
            throw new EmailAlreadyExistsException(
                    "Email " + email + " sudah dipakai pengguna lain. Pakai email lain.");
        }
    }

    /** Admin tidak boleh mengubah role akunnya sendiri (KNF-03). */
    private void pastikanBukanAdminSendiri(User target, User admin, Role roleBaru) {
        if (target.getId().equals(admin.getId()) && roleBaru != target.getRole()) {
            throw new AturanAkunException("Kamu tidak bisa mengubah role akun sendiri.");
        }
    }

    /** Admin aktif terakhir tidak boleh diturunkan, supaya platform tidak kehilangan Admin. */
    private void pastikanAdminTerakhirBolehDiubah(User target, Role roleBaru) {
        boolean turunDariAdmin = target.getRole() == Role.ADMIN && roleBaru != Role.ADMIN;
        if (target.isActive() && turunDariAdmin
                && userRepository.countByRoleAndActiveTrue(Role.ADMIN) <= 1) {
            throw new AturanAkunException("Admin aktif terakhir tidak bisa diturunkan.");
        }
    }

    /** Owner yang masih memegang bisnis tidak boleh diubah jadi role lain. */
    private void pastikanOwnerBisnisBolehDiubah(User target, Role roleBaru) {
        if (target.getRole() == Role.OWNER && roleBaru != Role.OWNER
                && businessRepository.existsByOwner(target)) {
            throw new AturanAkunException(
                    "Owner ini masih memegang bisnis, role-nya tidak bisa diubah.");
        }
    }
}
