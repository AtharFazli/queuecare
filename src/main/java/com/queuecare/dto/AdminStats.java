package com.queuecare.dto;

import com.queuecare.entity.Reservation;
import java.util.List;

/** Angka dashboard Admin. */
public record AdminStats(long totalPengguna, long customer, long owner,
                         long totalBisnis, long bisnisActive, long bisnisPending, long bisnisInactive,
                         long totalReservasi, List<Reservation> aktivitasTerbaru) {
}
