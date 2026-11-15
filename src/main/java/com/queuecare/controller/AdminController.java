package com.queuecare.controller;

import com.queuecare.dto.CategoryForm;
import com.queuecare.entity.Category;
import com.queuecare.enums.BusinessStatus;
import com.queuecare.service.BusinessService;
import com.queuecare.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CategoryService categoryService;
    private final BusinessService businessService;

    public AdminController(CategoryService categoryService, BusinessService businessService) {
        this.categoryService = categoryService;
        this.businessService = businessService;
    }

    // ===== kategori =====

    @GetMapping("/categories")
    public String kategori(@RequestParam(value = "ubah", required = false) Long ubah, Model model) {
        model.addAttribute("kategori", categoryService.semua());
        model.addAttribute("ubahId", ubah);
        CategoryForm form = new CategoryForm();
        if (ubah != null) {
            Category category = categoryService.cari(ubah);
            if (category != null) {
                form.setName(category.getName());
            }
        }
        model.addAttribute("categoryForm", form);
        return "admin/categories";
    }

    @PostMapping("/categories")
    public String simpanKategori(@RequestParam(value = "id", required = false) Long id,
                                 @Valid @ModelAttribute("categoryForm") CategoryForm form,
                                 BindingResult hasil, Model model, RedirectAttributes flash) {
        if (hasil.hasErrors()) {
            model.addAttribute("kategori", categoryService.semua());
            model.addAttribute("ubahId", id);
            return "admin/categories";
        }
        try {
            categoryService.simpan(id, form);
        } catch (AccessDeniedException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/categories";
        }
        flash.addFlashAttribute("sukses", "Kategori disimpan.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/hapus")
    public String hapusKategori(@PathVariable Long id, RedirectAttributes flash) {
        try {
            categoryService.hapus(id);
        } catch (AccessDeniedException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/categories";
        }
        flash.addFlashAttribute("sukses", "Kategori dihapus.");
        return "redirect:/admin/categories";
    }

    // ===== status bisnis =====

    @GetMapping("/businesses")
    public String bisnis(Model model) {
        model.addAttribute("bisnis", businessService.semuaBisnis());
        return "admin/businesses";
    }

    /** Admin mengubah status bisnis, bukan pemiliknya (R12). */
    @PostMapping("/businesses/{id}/status")
    public String statusBisnis(@PathVariable Long id,
                               @RequestParam("status") BusinessStatus status,
                               RedirectAttributes flash) {
        try {
            businessService.ubahStatusBisnis(id, status);
        } catch (AccessDeniedException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/businesses";
        }
        flash.addFlashAttribute("sukses", "Status bisnis diubah menjadi " + status + ".");
        return "redirect:/admin/businesses";
    }
}
