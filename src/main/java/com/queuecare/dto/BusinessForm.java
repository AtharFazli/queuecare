package com.queuecare.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class BusinessForm {

    @NotBlank(message = "Nama bisnis wajib diisi.")
    @Size(max = 150, message = "Nama bisnis maksimal 150 karakter.")
    private String name;

    @NotNull(message = "Kategori wajib dipilih.")
    private Long categoryId;

    private String description;

    private String address;

    @Pattern(regexp = "[0-9+()\\-\\s]{0,20}", message = "Nomor telepon tidak valid.")
    private String phone;

    @NotNull(message = "Kapasitas wajib diisi.")
    @Min(value = 0, message = "Kapasitas minimal 0.")
    private Integer maxCapacity;

    @NotNull(message = "Jumlah petugas aktif wajib diisi.")
    @Min(value = 0, message = "Jumlah petugas minimal 0.")
    private Integer activeStaffCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

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
