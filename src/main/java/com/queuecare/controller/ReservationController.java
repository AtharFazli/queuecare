package com.queuecare.controller;

import com.queuecare.entity.Service;
import com.queuecare.repository.ServiceRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Fase 3 hanya menyiapkan halaman sementara. Alur reservasi penuh menyusul di Fase 4.
 */
@Controller
@RequestMapping("/reservation")
public class ReservationController {

    private final ServiceRepository serviceRepository;

    public ReservationController(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @GetMapping("/create/{serviceId}")
    public String buat(@PathVariable Long serviceId, Model model) {
        Service service = serviceRepository.findById(serviceId).orElse(null);
        model.addAttribute("layanan", service);
        return "reservation/create";
    }
}
