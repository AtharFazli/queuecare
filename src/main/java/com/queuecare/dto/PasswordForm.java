package com.queuecare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Ganti password milik sendiri (R2). */
public class PasswordForm {

    @NotBlank(message = "Password lama wajib diisi.")
    private String passwordLama;

    @NotBlank(message = "Password baru wajib diisi.")
    @Size(min = 8, message = "Password baru minimal 8 karakter.")
    private String passwordBaru;

    @NotBlank(message = "Konfirmasi password wajib diisi.")
    private String konfirmasi;

    public String getPasswordLama() {
        return passwordLama;
    }

    public void setPasswordLama(String passwordLama) {
        this.passwordLama = passwordLama;
    }

    public String getPasswordBaru() {
        return passwordBaru;
    }

    public void setPasswordBaru(String passwordBaru) {
        this.passwordBaru = passwordBaru;
    }

    public String getKonfirmasi() {
        return konfirmasi;
    }

    public void setKonfirmasi(String konfirmasi) {
        this.konfirmasi = konfirmasi;
    }
}
