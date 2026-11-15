package com.queuecare.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Membuang pesan flash yang masih tertahan sebelum ada aksi POST baru.
 * Flash Spring disimpan di session dan hanya diambil oleh request yang cocok dengan
 * halaman tujuan, jadi pesan lama bisa muncul lagi di halaman berikutnya.
 */
@Component
public class FlashBersihInterceptor implements HandlerInterceptor {

    // Nama atribut session milik SessionFlashMapManager (Spring Web MVC 6).
    private static final String KUNCI_FLASH =
            "org.springframework.web.servlet.support.SessionFlashMapManager.FLASH_MAPS";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute(KUNCI_FLASH);
            }
        }
        return true;
    }
}
