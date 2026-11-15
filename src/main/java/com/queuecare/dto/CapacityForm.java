package com.queuecare.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CapacityForm {

    @NotNull(message = "Kapasitas wajib diisi.")
    @Min(value = 0, message = "Kapasitas minimal 0.")
    private Integer maxCapacity;

    @NotNull(message = "Jumlah petugas aktif wajib diisi.")
    @Min(value = 0, message = "Jumlah petugas minimal 0.")
    private Integer activeStaffCount;

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getActiveStaffCount() {
        return activeStaffCount;
    }

    public void setActiveStaffCount(Integer activeStaffCount) {
        this.activeStaffCount = activeStaffCount;
    }
}
