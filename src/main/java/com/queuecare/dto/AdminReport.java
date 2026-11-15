package com.queuecare.dto;

import java.time.LocalDate;
import java.util.List;

/** Laporan Admin dalam rentang tanggal (reservation_date). */
public record AdminReport(LocalDate dari, LocalDate sampai,
                          long totalBisnis, long totalPengguna, long totalReservasi,
                          List<TotalPerHari> reservasiPerHari,
                          List<TotalStatusAntrean> statusAntrean) {
}
