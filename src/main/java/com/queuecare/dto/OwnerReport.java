package com.queuecare.dto;

import java.time.LocalDate;
import java.util.List;

/** Laporan Owner dalam rentang tanggal (reservation_date). */
public record OwnerReport(LocalDate dari, LocalDate sampai,
                          long totalReservasi, long reservasiHariIni,
                          long selesai, long dibatalkan, long tidakHadir,
                          long pelangganUnik, List<TotalLayanan> layananTerpopuler) {
}
