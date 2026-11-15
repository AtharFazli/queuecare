package com.queuecare.service;

import com.queuecare.dto.AdminUserForm;
import com.queuecare.entity.User;
import com.queuecare.enums.Role;
import com.queuecare.exception.AturanAkunException;
import com.queuecare.exception.EmailAlreadyExistsException;
import com.queuecare.repository.BusinessRepository;
import com.queuecare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/** Aturan keselamatan pengelolaan akun (R12, KNF-03). */
@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BusinessRepository businessRepository;

    private AdminUserService service;

    @BeforeEach
    void setUp() {
        service = new AdminUserService(userRepository, businessRepository, new BCryptPasswordEncoder());
    }

    private AdminUserForm form(String nama, String email, Role role) {
        AdminUserForm form = new AdminUserForm();
        form.setName(nama);
        form.setEmail(email);
        form.setRole(role.name());
        return form;
    }

    private User user(Long id, Role role, boolean aktif) {
        User user = new User("Nama", id + "@queuecare.id", "hash", role);
        ReflectionTestUtils.setField(user, "id", id);
        user.setActive(aktif);
        return user;
    }

    @Test
    void tambahMenolakEmailDuplikat() {
        when(userRepository.existsByEmail("baru@queuecare.id")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> service.tambah(form("Baru", "baru@queuecare.id", Role.OWNER)));
    }

    @Test
    void tambahMenolakPasswordPendek() {
        AdminUserForm form = form("Baru", "baru@queuecare.id", Role.OWNER);
        form.setPassword("abc");

        assertThrows(AturanAkunException.class, () -> service.tambah(form));
    }

    @Test
    void adminTidakBisaMenonaktifkanDiriSendiri() {
        User admin = user(1L, Role.ADMIN, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThrows(AturanAkunException.class, () -> service.ubahStatus(1L, false, admin));
        assertEquals(true, admin.isActive(), "akun sendiri tidak boleh berubah");
    }

    @Test
    void adminTidakBisaMengubahRoleDiriSendiri() {
        User admin = user(1L, Role.ADMIN, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.existsByEmailAndIdNot("admin@queuecare.id", 1L)).thenReturn(false);

        assertThrows(AturanAkunException.class,
                () -> service.ubah(1L, form("Admin", "admin@queuecare.id", Role.OWNER), admin));
        assertEquals(Role.ADMIN, admin.getRole());
    }

    @Test
    void adminAktifTerakhirTidakBisaDinonaktifkan() {
        User admin = user(1L, Role.ADMIN, true);
        User pelaku = user(2L, Role.ADMIN, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.countByRoleAndActiveTrue(Role.ADMIN)).thenReturn(1L);

        assertThrows(AturanAkunException.class, () -> service.ubahStatus(1L, false, pelaku));
        assertEquals(true, admin.isActive());
    }

    @Test
    void adminAktifTerakhirTidakBisaDiturunkan() {
        User admin = user(1L, Role.ADMIN, true);
        User pelaku = user(2L, Role.ADMIN, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.existsByEmailAndIdNot("admin@queuecare.id", 1L)).thenReturn(false);
        when(userRepository.countByRoleAndActiveTrue(Role.ADMIN)).thenReturn(1L);

        assertThrows(AturanAkunException.class,
                () -> service.ubah(1L, form("Admin", "admin@queuecare.id", Role.CUSTOMER), pelaku));
        assertEquals(Role.ADMIN, admin.getRole());
    }

    @Test
    void ownerYangMasihPunyaBisnisTidakBisaDiubahRole() {
        User owner = user(3L, Role.OWNER, true);
        User pelaku = user(2L, Role.ADMIN, true);
        when(userRepository.findById(3L)).thenReturn(Optional.of(owner));
        when(userRepository.existsByEmailAndIdNot("owner@queuecare.id", 3L)).thenReturn(false);
        when(businessRepository.existsByOwner(owner)).thenReturn(true);

        assertThrows(AturanAkunException.class,
                () -> service.ubah(3L, form("Owner", "owner@queuecare.id", Role.CUSTOMER), pelaku));
        assertEquals(Role.OWNER, owner.getRole());
    }

    @Test
    void ubahPasswordOpsionalKosongTidakMengubahPassword() {
        User target = user(3L, Role.OWNER, true);
        User pelaku = user(2L, Role.ADMIN, true);
        when(userRepository.findById(3L)).thenReturn(Optional.of(target));
        when(userRepository.existsByEmailAndIdNot("owner@queuecare.id", 3L)).thenReturn(false);
        lenient().when(businessRepository.existsByOwner(any())).thenReturn(false);

        service.ubah(3L, form("Owner Baru", "owner@queuecare.id", Role.OWNER), pelaku);

        assertEquals("hash", target.getPasswordHash(), "password kosong berarti tidak diubah");
    }
}
