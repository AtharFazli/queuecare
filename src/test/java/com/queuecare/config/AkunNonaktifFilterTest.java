package com.queuecare.config;

import com.queuecare.entity.User;
import com.queuecare.enums.Role;
import com.queuecare.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AkunNonaktifFilterTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;
    @Mock
    private HttpSession session;

    private AkunNonaktifFilter filter;

    @BeforeEach
    void setUp() {
        filter = new AkunNonaktifFilter(userRepository);
    }

    @AfterEach
    void bersihkan() {
        SecurityContextHolder.clearContext();
    }

    private void login(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, "x",
                        List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))));
    }

    private User pengguna(String email, boolean aktif) {
        User user = new User("Uji", email, "hash", Role.CUSTOMER);
        user.setActive(aktif);
        return user;
    }

    @Test
    void akunNonaktifDitolakSesiDiputusDanRantaiDihentikan() throws Exception {
        login("nonaktif@queuecare.id");
        when(request.getContextPath()).thenReturn("");
        when(request.getSession(false)).thenReturn(session);
        // Filter memeriksa getSession(false), lalu memutus sesi lewat getSession() tanpa argumen.
        when(request.getSession()).thenReturn(session);
        when(userRepository.findByEmail("nonaktif@queuecare.id"))
                .thenReturn(Optional.of(pengguna("nonaktif@queuecare.id", false)));

        filter.doFilterInternal(request, response, chain);

        verify(response).sendRedirect("/login?disabled");
        verify(session).invalidate();
        verify(chain, never()).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void akunAktifLanjutTanpaMengubahSesi() throws Exception {
        login("aktif@queuecare.id");
        when(userRepository.findByEmail("aktif@queuecare.id"))
                .thenReturn(Optional.of(pengguna("aktif@queuecare.id", true)));

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendRedirect(any());
        verify(request, never()).getSession(false);
    }

    @Test
    void tidakLoginLanjutTanpaMenyentuhRepository() throws Exception {
        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(userRepository);
        verify(response, never()).sendRedirect(any());
    }

    @Test
    void anonymousLanjutTanpaMenyentuhRepository() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
                "kunci", "anonymousUser", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(userRepository);
    }

    @Test
    void emailTidakDitemukanLanjutDanTidakMengubahSesi() throws Exception {
        login("hantu@queuecare.id");
        when(userRepository.findByEmail("hantu@queuecare.id")).thenReturn(Optional.empty());

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendRedirect(any());
        verify(request, never()).getSession(false);
    }
}
