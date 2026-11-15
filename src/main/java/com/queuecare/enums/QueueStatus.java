package com.queuecare.enums;

/** Status antrean beserta kelas lencana yang sesuai di DESIGN.md. */
public enum QueueStatus {
    WAITING("lencana--waiting"),
    CALLED("lencana--called"),
    IN_SERVICE("lencana--in-service"),
    COMPLETED("lencana--completed"),
    CANCELLED("lencana--cancelled"),
    NO_SHOW("lencana--no-show");

    private final String kelas;

    QueueStatus(String kelas) {
        this.kelas = kelas;
    }

    public String getKelas() {
        return kelas;
    }
}
