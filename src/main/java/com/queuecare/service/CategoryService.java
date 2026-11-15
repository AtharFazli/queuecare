package com.queuecare.service;

import com.queuecare.dto.CategoryForm;
import com.queuecare.entity.Category;
import com.queuecare.repository.BusinessRepository;
import com.queuecare.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BusinessRepository businessRepository;

    public CategoryService(CategoryRepository categoryRepository, BusinessRepository businessRepository) {
        this.categoryRepository = categoryRepository;
        this.businessRepository = businessRepository;
    }

    public List<Category> semua() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    public Category cari(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Transactional
    public Category simpan(Long id, CategoryForm form) {
        Category category;
        if (id == null) {
            category = new Category(form.getName());
        } else {
            category = categoryRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Kategori tidak ditemukan."));
            category.setName(form.getName());
        }
        Long idLain = id == null ? -1L : id;
        if (categoryRepository.existsByNameAndIdNot(category.getName(), idLain)) {
            throw new IllegalStateException("Kategori " + category.getName() + " sudah ada.");
        }
        return categoryRepository.save(category);
    }

    /** Kategori yang masih dipakai bisnis tidak bisa dihapus. */
    @Transactional
    public void hapus(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Kategori tidak ditemukan."));
        if (businessRepository.existsByCategory(category)) {
            throw new IllegalStateException("Kategori masih dipakai bisnis, tidak bisa dihapus.");
        }
        categoryRepository.delete(category);
    }
}
