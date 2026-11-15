package com.queuecare.service;

import com.queuecare.entity.Business;
import com.queuecare.entity.User;
import com.queuecare.repository.BusinessRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Menentukan bisnis aktif seorang Owner. Pilihan disimpan di session, tapi setiap
 * pemakaian tetap dicek kepemilikannya supaya bisnis milik Owner lain tidak bisa dipakai (KNF-05).
 */
@Service
public class ActiveBusiness {

    public static final String KUNCI_SESSION = "bisnisAktifId";

    private final BusinessRepository businessRepository;
    private final UserContext userContext;

    public ActiveBusiness(BusinessRepository businessRepository, UserContext userContext) {
        this.businessRepository = businessRepository;
        this.userContext = userContext;
    }

    @Transactional(readOnly = true)
    public List<Business> semuaMilikSaya() {
        return businessRepository.findByOwnerOrderByIdAsc(userContext.currentUser());
    }

    /** Menyimpan pilihan di session setelah memastikan bisnis ini milik Owner yang login. */
    @Transactional
    public Business pilih(HttpSession session, Long id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bisnis tidak ditemukan."));
        userContext.pastikanPemilik(business.getOwner());
        session.setAttribute(KUNCI_SESSION, id);
        return business;
    }

    /**
     * Bisnis aktif untuk halaman Owner. Kalau Owner hanya punya satu bisnis, bisnis itu
     * dipilih otomatis. Kalau belum punya pilihan dan bisnisnya lebih dari satu, kembalikan null.
     */
    @Transactional(readOnly = true)
    public Business bisnisAktif(HttpSession session) {
        User owner = userContext.currentUser();
        List<Business> semua = businessRepository.findByOwnerOrderByIdAsc(owner);
        if (semua.isEmpty()) {
            return null;
        }
        Long id = (Long) session.getAttribute(KUNCI_SESSION);
        if (id == null) {
            if (semua.size() == 1) {
                session.setAttribute(KUNCI_SESSION, semua.get(0).getId());
                return semua.get(0);
            }
            return null;
        }
        Business business = businessRepository.findById(id).orElse(null);
        if (business == null) {
            session.removeAttribute(KUNCI_SESSION);
            return semua.size() == 1 ? semua.get(0) : null;
        }
        if (!business.getOwner().getId().equals(owner.getId())) {
            throw new AccessDeniedException("Kamu tidak punya hak akses ke bisnis ini.");
        }
        return business;
    }
}
