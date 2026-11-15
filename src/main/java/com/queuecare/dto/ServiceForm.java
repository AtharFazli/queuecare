package com.queuecare.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ServiceForm {

    @NotBlank(message = "Nama layanan wajib diisi.")
    @Size(max = 150, message = "Nama layanan maksimal 150 karakter.")
    private String name;

    private String description;

    @DecimalMin(value = "0.00", message = "Harga tidak boleh negatif.")
    private BigDecimal price;

    @Min(value = 1, message = "Durasi harus lebih dari 0 menit.")
    private Integer estimatedDurationMinutes;

    private boolean active = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }

    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
