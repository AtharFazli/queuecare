package com.queuecare.controller;

import com.queuecare.dto.ReservationForm;
import com.queuecare.entity.Reservation;
import com.queuecare.entity.Service;
import com.queuecare.service.QueueService;
import com.queuecare.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;
    private final QueueService queueService;

    public ReservationController(ReservationService reservationService, QueueService queueService) {
        this.reservationService = reservationService;
        this.queueService = queueService;
    }

    @GetMapping("/create/{serviceId}")
    public String form(@PathVariable Long serviceId,
                       @RequestParam(value = "tanggal", required = false) String tanggal,
                       Model model) {
        Service service = reservationService.layananAktif(serviceId);
        isiModel(model, service, parseTanggal(tanggal), null);
        return "reservation/create";
    }

    @PostMapping("/create/{serviceId}")
    public String buat(@PathVariable Long serviceId,
                       @Valid @ModelAttribute("form") ReservationForm form,
                       BindingResult hasil, Model model) {
        Service service;
        try {
            service = reservationService.layananAktif(serviceId);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "reservation/create";
        }
        if (hasil.hasErrors()) {
            isiModel(model, service, form.getTanggal(), form);
            return "reservation/create";
        }
        try {
            Reservation reservation = reservationService.buat(serviceId, form);
            return "redirect:/reservation/success/" + reservation.getId();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            isiModel(model, service, form.getTanggal(), form);
            return "reservation/create";
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("error", "Reservasi bentrok dengan pelanggan lain. Silakan coba lagi.");
            isiModel(model, service, form.getTanggal(), form);
            return "reservation/create";
        }
    }

    @GetMapping("/success/{id}")
    public String sukses(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.milikSaya(id);
        model.addAttribute("reservasi", reservation);
        model.addAttribute("antrean", reservationService.antrean(reservation));
        model.addAttribute("bisaPantau", bisaPantau(reservation));
        return "reservation/success";
    }

    private boolean bisaPantau(Reservation reservation) {
        var antrean = reservationService.antrean(reservation);
        return antrean != null && !queueService.sudahFinal(antrean);
    }

    private void isiModel(Model model, Service service, LocalDate tanggal, ReservationForm form) {
        model.addAttribute("layanan", service);
        model.addAttribute("bisnis", service.getBusiness());
        model.addAttribute("tanggal", tanggal);
        model.addAttribute("slot", reservationService.slotUntuk(service.getBusiness(), tanggal));
        model.addAttribute("form", form == null ? new ReservationForm() : form);
    }

    /** Tanggal GET diparse sebagai yyyy-MM-dd. Nilai kosong atau salah memakai hari ini. */
    private LocalDate parseTanggal(String nilai) {
        if (nilai == null || nilai.isBlank()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(nilai);
        } catch (DateTimeParseException ex) {
            return LocalDate.now();
        }
    }
}
