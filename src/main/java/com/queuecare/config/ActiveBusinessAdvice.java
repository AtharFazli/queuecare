package com.queuecare.config;

import com.queuecare.entity.Business;
import com.queuecare.service.ActiveBusiness;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Mengirim bisnis aktif Owner ke tampilan (sidebar) di semua halaman. */
@ControllerAdvice
public class ActiveBusinessAdvice {

    private final ActiveBusiness activeBusiness;

    public ActiveBusinessAdvice(ActiveBusiness activeBusiness) {
        this.activeBusiness = activeBusiness;
    }

    @ModelAttribute("bisnisAktif")
    public Business bisnisAktif(HttpSession session, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return activeBusiness.bisnisAktif(session);
    }
}
