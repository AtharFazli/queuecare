package com.queuecare.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CategoryForm {

    @NotBlank(message = "Nama kategori wajib diisi.")
    @Size(max = 100, message = "Nama kategori maksimal 100 karakter.")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
