package com.queuecare.repository;

import com.queuecare.entity.Business;
import com.queuecare.entity.Category;
import com.queuecare.entity.User;
import com.queuecare.enums.BusinessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {

    List<Business> findByOwnerOrderByIdAsc(User owner);

    List<Business> findAllByOrderByIdAsc();

    long countByStatus(BusinessStatus status);

    /** Katalog publik: hanya bisnis ACTIVE, dengan pencarian nama dan filter kategori opsional. */
    @Query("select b from Business b join fetch b.owner join fetch b.category "
            + "where b.status = :status "
            + "and (:q is null or lower(b.name) like lower(concat('%', :q, '%'))) "
            + "and (:kategoriId is null or b.category.id = :kategoriId) "
            + "order by b.name")
    List<Business> cariPublik(@Param("status") BusinessStatus status,
                              @Param("q") String q,
                              @Param("kategoriId") Long kategoriId);

    /** Detail publik: bisnis non-ACTIVE dianggap tidak ada. */
    @Query("select b from Business b join fetch b.owner join fetch b.category "
            + "where b.id = :id and b.status = :status")
    Optional<Business> cariPublikById(@Param("id") Long id, @Param("status") BusinessStatus status);

    /** Kategori yang dipakai bisnis ACTIVE, untuk filter katalog. */
    @Query("select distinct b.category from Business b join b.category "
            + "where b.status = :status order by b.category.name")
    List<Category> kategoriDipakai(@Param("status") BusinessStatus status);

    boolean existsByCategory(Category category);

    /** Owner yang masih memegang bisnis tidak boleh diubah jadi role lain. */
    boolean existsByOwner(User owner);

    /** Mengunci baris bisnis supaya hitung kapasitas dan nomor antrean tidak berlomba. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Business b where b.id = :id")
    Optional<Business> lockById(@Param("id") Long id);
}
