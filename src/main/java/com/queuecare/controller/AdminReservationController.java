package com.queuecare.controller;

import com.queuecare.entity.Business;
import com.queuecare.enums.ReservationStatus;
import com.queuecare.repository.BusinessRepository;
import com.queuecare.service.AdminReservationService;
import com.queuecare.util.RentangTanggal;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

/** Daftar reservasi Admin dengan filter dan paginasi (R12). */
@Controller
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private static final List<String> STATUS = List.of("CONFIRMED", "COMPLETED", "CANCELLED", "PENDING");

    private final AdminReservationService adminReservationService;
    private final BusinessRepository businessRepository;

    public AdminReservationController(AdminReservationService adminReservationService,
                                      BusinessRepository businessRepository) {
        this.adminReservationService = adminReservationService;
        this.businessRepository = businessRepository;
    }

    @GetMapping
    public String daftar(@RequestParam(value = "dari", required = false) String dari,
                         @RequestParam(value = "sampai", required = false) String sampai,
                         @RequestParam(value = "bisnis", required = false) Long bisnisId,
                         @RequestParam(value = "status", required = false) String status,
                         @RequestParam(value = "hal", defaultValue = "1") int hal,
                         Model model) {
        ReservationStatus statusReservasi = parseStatus(status);
        model.addAttribute("baris", adminReservationService.daftar(
                RentangTanggal.parse(dari, null),
                RentangTanggal.parse(sampai, null),
                bisnisId, statusReservasi, hal));
        model.addAttribute("bisnis", businessRepository.findAllByOrderByIdAsc());
        model.addAttribute("status", STATUS);
        model.addAttribute("dari", dari);
        model.addAttribute("sampai", sampai);
        model.addAttribute("bisnisId", bisnisId);
        model.addAttribute("statusParam", status);
        return "admin/reservations";
    }

    /** Status tidak dikenal dianggap tanpa filter. */
    private ReservationStatus parseStatus(String nilai) {
        if (nilai == null || nilai.isBlank()) {
            return null;
        }
        try {
            return ReservationStatus.valueOf(nilai);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
