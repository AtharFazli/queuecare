package com.queuecare.dto;

import java.time.LocalDate;

/** Jumlah per hari untuk laporan Admin. */
public record TotalPerHari(LocalDate tanggal, long jumlah) {
}
