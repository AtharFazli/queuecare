package com.queuecare.config;

import com.queuecare.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.SavedRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // R1: hak akses diperiksa di backend, bukan hanya disembunyikan di tampilan.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/businesses", "/businesses/**", "/css/**", "/js/**", "/error", "/403").permitAll()
                // Aset merek (logo, favicon, ikon PWA) harus bisa diambil anonim di semua halaman.
                .requestMatchers("/images/**", "/favicon.ico", "/favicon.svg", "/favicon-*.png", "/apple-touch-icon.png", "/icon-*.png", "/maskable-*.png", "/site.webmanifest").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/owner/**").hasRole("OWNER")
                .requestMatchers("/reservation/**", "/reservations/**", "/queue/**").hasRole("CUSTOMER")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(roleBasedSuccessHandler())
                .failureHandler(pesanGagalLogin())
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID"))
            .exceptionHandling(handling -> handling.accessDeniedHandler(accessDeniedHandler()));

        // Akun yang baru dinonaktifkan Admin ditolak sejak request berikutnya.
        http.addFilterAfter(new AkunNonaktifFilter(userRepository), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // Akun nonaktif mendapat pesan khusus; kesalahan login lain tetap generik
    // supaya tidak membocorkan apakah email terdaftar (KNF-03).
    private AuthenticationFailureHandler pesanGagalLogin() {
        return (request, response, exception) -> {
            String target = exception instanceof org.springframework.security.authentication.DisabledException
                    ? "/login?disabled"
                    : "/login?error";
            response.sendRedirect(request.getContextPath() + target);
        };
    }

    // Halaman 403 sekaligus status HTTP 403, supaya penolakan akses jelas (R1).
    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, exception) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.getRequestDispatcher("/403").forward(request, response);
        };
    }

    // R2 + alur Fase 1 + SavedRequest: Customer kembali ke halaman yang tadi dituju
    // bila ada dan masih di aplikasi sendiri; Owner/Admin selalu ke dashboard role.
    AuthenticationSuccessHandler roleBasedSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            if (punyaPeran(authentication, "ROLE_ADMIN")) {
                response.sendRedirect(request.getContextPath() + "/admin");
                return;
            }
            if (punyaPeran(authentication, "ROLE_OWNER")) {
                response.sendRedirect(request.getContextPath() + "/owner");
                return;
            }
            String tujuan = tujuanTersimpan(request);
            response.sendRedirect(request.getContextPath() + (tujuan != null ? tujuan : "/customer"));
        };
    }

    /** Jalur tujuan tersimpan yang masih di aplikasi sendiri; null bila tidak aman/tidak ada. */
    static String tujuanTersimpan(HttpServletRequest request) {
        Object tersimpan = request.getSession(false) == null ? null
                : request.getSession(false).getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if (!(tersimpan instanceof SavedRequest)) {
            return null;
        }
        String tujuan = ((SavedRequest) tersimpan).getRedirectUrl();
        String jalur = tujuan == null ? null : tujuan;
        if (jalur != null && jalur.startsWith(request.getContextPath() + "/")) {
            jalur = jalur.substring(request.getContextPath().length());
        } else if (jalur != null && !jalur.startsWith("/")) {
            return null;
        }
        if (jalur == null || jalur.isBlank() || !jalur.startsWith("/") || jalur.contains("//") || jalur.contains("/../")) {
            return null;
        }
        return jalur;
    }

    private static boolean punyaPeran(Authentication authentication, String peran) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(peran));
    }
}
