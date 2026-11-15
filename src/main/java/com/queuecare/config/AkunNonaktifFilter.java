package com.queuecare.config;

import com.queuecare.entity.User;
import com.queuecare.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Akun yang dinonaktifkan lewat Admin langsung ditolak pada request berikutnya,
 * bukan hanya saat login (R1, R12).
 */
public class AkunNonaktifFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public AkunNonaktifFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean nonaktif = auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)
                && userRepository.findByEmail(auth.getName())
                    .map(user -> !user.isActive())
                    .orElse(false);

        if (nonaktif) {
            SecurityContextHolder.clearContext();
            if (request.getSession(false) != null) {
                request.getSession().invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login?disabled");
            return;
        }
        chain.doFilter(request, response);
    }
}
