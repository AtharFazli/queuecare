package com.queuecare.repository;

import com.queuecare.entity.Business;
import com.queuecare.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByBusinessOrderByIdAsc(Business business);

    /** Hanya layanan aktif yang boleh tampil di katalog publik. */
    List<Service> findByBusinessAndActiveTrueOrderByIdAsc(Business business);

    /** Layanan beserta bisnis dan kategori, untuk form reservasi. */
    @Query("select s from Service s join fetch s.business b join fetch b.category where s.id = :id")
    Optional<Service> findByIdWithBusiness(@Param("id") Long id);
}
