package com.queuecare.repository;

import com.queuecare.entity.User;
import com.queuecare.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    /** Email dipakai pengguna lain, dipakai saat ubah profil atau ubah pengguna. */
    boolean existsByEmailAndIdNot(String email, Long id);

    long countByRole(Role role);

    /** Aturan keselamatan: Admin aktif terakhir tidak boleh dinonaktifkan atau diturunkan. */
    long countByRoleAndActiveTrue(Role role);

    /** Daftar pengguna Admin: pencarian nama atau email, filter role, paginasi 20 per halaman. */
    @Query("select u from User u "
            + "where (:q is null or lower(u.name) like lower(concat('%', :q, '%')) "
            + "          or lower(u.email) like lower(concat('%', :q, '%'))) "
            + "and (:role is null or u.role = :role) "
            + "order by u.id")
    Page<User> cariUntukAdmin(@Param("q") String q, @Param("role") Role role, Pageable pageable);
}
