package com.queuecare.dto;

import com.queuecare.enums.QueueStatus;

/** Jumlah antrean per status untuk ringkasan platform. */
public record TotalStatusAntrean(QueueStatus status, long jumlah) {
}
