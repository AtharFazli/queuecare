package com.queuecare.service;

import com.queuecare.entity.Business;
import com.queuecare.entity.Category;
import com.queuecare.entity.Service;
import com.queuecare.enums.BusinessStatus;
import com.queuecare.repository.BusinessRepository;
import com.queuecare.repository.ServiceRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Katalog publik. Semua query di sini dibatasi ke bisnis ACTIVE dan layanan aktif,
 * jadi bisnis PENDING/INACTIVE dan layanan nonaktif tidak bisa bocor ke publik.
 */
@org.springframework.stereotype.Service
public class CatalogService {

    private final BusinessRepository businessRepository;
    private final ServiceRepository serviceRepository;

    public CatalogService(BusinessRepository businessRepository, ServiceRepository serviceRepository) {
        this.businessRepository = businessRepository;
        this.serviceRepository = serviceRepository;
    }

    @Transactional(readOnly = true)
    public List<Business> katalog(String q, Long kategoriId) {
        String kata = (q == null || q.isBlank()) ? null : q.trim();
        return businessRepository.cariPublik(BusinessStatus.ACTIVE, kata, kategoriId);
    }

    @Transactional(readOnly = true)
    public Optional<Business> detail(Long id) {
        return businessRepository.cariPublikById(id, BusinessStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Service> layananAktif(Business business) {
        return serviceRepository.findByBusinessAndActiveTrueOrderByIdAsc(business);
    }

    @Transactional(readOnly = true)
    public List<Category> kategoriUntukFilter() {
        return businessRepository.kategoriDipakai(BusinessStatus.ACTIVE);
    }
}
