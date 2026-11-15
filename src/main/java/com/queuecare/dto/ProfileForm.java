package com.queuecare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Ubah nama dan email profil milik sendiri (R2). */
public class ProfileForm {

    @NotBlank(message = "Nama wajib diisi.")
    @Size(max = 100, message = "Nama maksimal 100 karakter.")
    private String name;

    @NotBlank(message = "Email wajib diisi.")
    @Email(message = "Format email tidak valid.")
    @Size(max = 150, message = "Email maksimal 150 karakter.")
    private String email;

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
}
