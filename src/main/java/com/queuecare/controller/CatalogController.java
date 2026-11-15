package com.queuecare.controller;

import com.queuecare.entity.Business;
import com.queuecare.entity.BusinessSchedule;
import com.queuecare.repository.BusinessScheduleRepository;
import com.queuecare.service.CatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class CatalogController {

    private final CatalogService catalogService;
    private final BusinessScheduleRepository scheduleRepository;

    public CatalogController(CatalogService catalogService, BusinessScheduleRepository scheduleRepository) {
        this.catalogService = catalogService;
        this.scheduleRepository = scheduleRepository;
    }

    @GetMapping("/businesses")
    public String katalog(@RequestParam(value = "q", required = false) String q,
                          @RequestParam(value = "kategori", required = false) Long kategoriId,
                          Model model) {
        model.addAttribute("bisnis", catalogService.katalog(q, kategoriId));
        model.addAttribute("kategori", catalogService.kategoriUntukFilter());
        model.addAttribute("q", q);
        model.addAttribute("kategoriId", kategoriId);
        return "businesses/index";
    }

    @GetMapping("/businesses/{id}")
    public String detail(@PathVariable Long id, Model model) {
        // Bisnis non-ACTIVE dianggap tidak ada untuk publik.
        Business business = catalogService.detail(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bisnis tidak ditemukan."));
        model.addAttribute("bisnis", business);
        model.addAttribute("layanan", catalogService.layananAktif(business));
        model.addAttribute("jadwal", scheduleRepository.findByBusinessOrderByIdAsc(business));
        return "businesses/detail";
    }
}
