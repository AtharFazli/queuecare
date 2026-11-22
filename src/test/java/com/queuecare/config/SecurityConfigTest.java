package com.queuecare.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** SavedRequest: pilihan target setelah login dan anti open redirect. */
class SecurityConfigTest {

    private final SecurityConfig config = new SecurityConfig(null);

    private MockHttpServletRequest request() {
        return new MockHttpServletRequest("GET", "/reservation/create/42");
    }

    private jakarta.servlet.http.HttpServletRequest requestDenganCache(MockHttpServletRequest request) {
        return request;
    }

    private void simpan(MockHttpServletRequest request, String redirectUrl) {
        SavedRequest tersimpan = mock(SavedRequest.class);
        when(tersimpan.getRedirectUrl()).thenReturn(redirectUrl);
        request.getSession(true).setAttribute("SPRING_SECURITY_SAVED_REQUEST", tersimpan);
    }

    private Authentication peran(String peran) {
        Authentication auth = mock(Authentication.class);
        when(auth.getAuthorities()).thenAnswer(inv -> List.of(new SimpleGrantedAuthority(peran)));
        return auth;
    }

    private String redirect(String peran, MockHttpServletRequest request) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        config.roleBasedSuccessHandler().onAuthenticationSuccess(request, response, peran(peran));
        return response.getRedirectedUrl();
    }

    @Test
    void customerKembaliKeFormReservasiYangDituju() throws Exception {
        MockHttpServletRequest request = request();
        simpan(request, "/reservation/create/42");

        assertEquals("/reservation/create/42", redirect("ROLE_CUSTOMER", request));
    }

    @Test
    void ownerDanAdminSelaluKeDashboardRole() throws Exception {
        MockHttpServletRequest request = request();
        simpan(request, "/reservation/create/42");

        assertEquals("/admin", redirect("ROLE_ADMIN", request));
        assertEquals("/owner", redirect("ROLE_OWNER", request));
    }

    @Test
    void customerTanpaTujuanKeDashboardCustomer() throws Exception {
        assertEquals("/customer", redirect("ROLE_CUSTOMER", request()));
    }

    @Test
    void tujuanTersimpanDomainLuarTidakDiikuti() throws Exception {
        MockHttpServletRequest request = request();
        simpan(request, "http://evil.example.com/steal");

        assertEquals("/customer", redirect("ROLE_CUSTOMER", request), "open redirect tidak boleh diikuti");
    }

    @Test
    void tujuanTersimpanDomainLainTidakDiikuti() throws Exception {
        MockHttpServletRequest request = request();
        simpan(request, "/reservation/create/42");

        assertEquals("/reservation/create/42", redirect("ROLE_CUSTOMER", request));
    }

    @Test
    void tanpaTujuanTersimpanKembaliNull() {
        assertNull(SecurityConfig.tujuanTersimpan(request()));
    }
}
