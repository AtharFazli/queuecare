package com.queuecare.dto;

/** Angka dashboard Owner untuk bisnis aktif. */
public record OwnerStats(long reservasiHariIni, long menunggu, long sedangDilayani, long selesaiHariIni) {
}
