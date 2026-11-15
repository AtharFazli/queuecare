package com.queuecare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dipakai Admin untuk menambah dan mengubah pengguna. Password opsional saat ubah:
 * kolom kosong berarti password tidak diubah.
 */
public class AdminUserForm {

    @NotBlank(message = "Nama wajib diisi.")
    @Size(max = 100, message = "Nama maksimal 100 karakter.")
    private String name;

    @NotBlank(message = "Email wajib diisi.")
    @Email(message = "Format email tidak valid.")
    @Size(max = 150, message = "Email maksimal 150 karakter.")
    private String email;

    @NotBlank(message = "Role wajib dipilih.")
    private String role;

    @Size(min = 8, message = "Password minimal 8 karakter.")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
