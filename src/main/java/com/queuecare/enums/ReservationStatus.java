package com.queuecare.enums;

/** Status reservasi beserta kelas lencana yang sesuai di DESIGN.md. */
public enum ReservationStatus {
    PENDING("lencana--waiting"),
    CONFIRMED("lencana--in-service"),
    CANCELLED("lencana--cancelled"),
    COMPLETED("lencana--completed");

    private final String kelas;

    ReservationStatus(String kelas) {
        this.kelas = kelas;
    }

    public String getKelas() {
        return kelas;
    }
}
