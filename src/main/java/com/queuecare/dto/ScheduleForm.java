package com.queuecare.dto;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public class ScheduleForm {

    @NotBlank(message = "Hari wajib dipilih.")
    private String dayOfWeek;

    @NotNull(message = "Jam buka wajib diisi.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime openTime;

    @NotNull(message = "Jam tutup wajib diisi.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime closeTime;

    private boolean closed;

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
