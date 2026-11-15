package com.queuecare.dto;

/** Status antrean terkini untuk halaman monitoring Customer. */
public record QueueStatusDto(String status, int posisi, String keterangan,
                             Integer estimasi, String pesan, int orangDepan) {
}
