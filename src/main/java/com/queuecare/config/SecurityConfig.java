package com.queuecare.config;

import com.queuecare.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

    // R2 + alur Fase 1: setelah login, pengguna diarahkan ke halaman sesuai role.
    private AuthenticationSuccessHandler roleBasedSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            String target = "/customer";
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                target = "/admin";
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_OWNER"))) {
                target = "/owner";
            }
            response.sendRedirect(request.getContextPath() + target);
        };
    }
}
