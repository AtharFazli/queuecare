package com.queuecare.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/** Satu baris daftar reservasi Admin, sudah lengkap dengan status antrean. */
public record BarisReservasi(String kode, String customer, String bisnis, String layanan,
                             LocalDate tanggal, LocalTime jam,
                             String statusReservasi, String statusAntrean) {
}
