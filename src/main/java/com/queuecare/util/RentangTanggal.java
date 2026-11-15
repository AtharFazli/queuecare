package com.queuecare.util;

import java.time.LocalDate;

/** Pembacaan dan validasi parameter tanggal laporan. */
public final class RentangTanggal {

    private RentangTanggal() {
    }

    /** Nilai kosong atau salah memakai nilai bawaan. */
    public static LocalDate parse(String nilai, LocalDate bawaan) {
        if (nilai == null || nilai.isBlank()) {
            return bawaan;
        }
        try {
            return LocalDate.parse(nilai);
        } catch (Exception ex) {
            return bawaan;
        }
    }

    /** Menolak tanggal awal setelah tanggal akhir. */
    public static String validasi(LocalDate dari, LocalDate sampai) {
        if (dari != null && sampai != null && dari.isAfter(sampai)) {
            return "Tanggal awal tidak boleh setelah tanggal akhir.";
        }
        return null;
    }
}
