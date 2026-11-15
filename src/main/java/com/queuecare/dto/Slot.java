package com.queuecare.dto;

import java.time.LocalTime;

/** Satu slot 30 menit di halaman reservasi. */
public class Slot {

    private final LocalTime jam;
    private final boolean penuh;
    private final boolean lewat;

    public Slot(LocalTime jam, boolean penuh, boolean lewat) {
        this.jam = jam;
        this.penuh = penuh;
        this.lewat = lewat;
    }

    public LocalTime getJam() {
        return jam;
    }

    public boolean isPenuh() {
        return penuh;
    }

    public boolean isLewat() {
        return lewat;
    }

    /** Label untuk pilihan slot, termasuk keterangan penuh atau lewat. */
    public String getLabel() {
        String teks = jam.toString();
        if (penuh) {
            return teks + " (Penuh)";
        }
        if (lewat) {
            return teks + " (Lewat)";
        }
        return teks;
    }
}
