package com.queuecare.controller;

import com.queuecare.entity.Reservation;
import com.queuecare.service.QueueService;
import com.queuecare.service.ReservationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reservations")
public class ReservationHistoryController {

    private final ReservationService reservationService;
    private final QueueService queueService;

    public ReservationHistoryController(ReservationService reservationService, QueueService queueService) {
        this.reservationService = reservationService;
        this.queueService = queueService;
    }

    @GetMapping
    public String riwayat(Model model) {
        model.addAttribute("daftar", reservationService.riwayat());
        return "reservations/index";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.milikSaya(id);
        model.addAttribute("reservasi", reservation);
        var antrean = reservationService.antrean(reservation);
        model.addAttribute("antrean", antrean);
        model.addAttribute("bisaPantau", antrean != null && !queueService.sudahFinal(antrean));
        return "reservations/detail";
    }

    @PostMapping("/{id}/batal")
    public String batal(@PathVariable Long id, RedirectAttributes flash) {
        try {
            reservationService.batalkan(id);
            flash.addFlashAttribute("sukses", "Reservasi dibatalkan.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/reservations";
    }
}
