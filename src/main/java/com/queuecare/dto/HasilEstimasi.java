package com.queuecare.dto;

/**
 * Hasil SmartWait. Bila menit != null, tampilkan angka. Bila menit null dan pesan
 * ad isi, tampilkan pesan khusus. Bila dua-duanya null, tidak ada estimasi.
 */
public record HasilEstimasi(Integer menit, String pesan) {
}
