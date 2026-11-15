package com.queuecare.controller;

import com.queuecare.entity.Business;
import com.queuecare.entity.Queue;
import com.queuecare.service.ActiveBusiness;
import com.queuecare.service.QueueService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/** Halaman antrean Owner untuk bisnis aktif (R7). */
@Controller
@RequestMapping("/owner/queues")
public class OwnerQueueController {

    private final QueueService queueService;
    private final ActiveBusiness activeBusiness;

    public OwnerQueueController(QueueService queueService, ActiveBusiness activeBusiness) {
        this.queueService = queueService;
        this.activeBusiness = activeBusiness;
    }

    private enum Aksi {
        PANGGIL, MULAI, SELESAI, TIDAK_HADIR, BATAL
    }

    @GetMapping
    public String daftar(@RequestParam(value = "tanggal", required = false) String tanggal,
                         HttpSession session, Model model) {
        Business business = activeBusiness.bisnisAktif(session);
        if (business == null) {
            return "redirect:/owner/businesses";
        }
        LocalDate hari = parseTanggal(tanggal);
        model.addAttribute("bisnis", business);
        model.addAttribute("tanggal", hari);
        model.addAttribute("antrean", queueService.antreanBisnis(business, hari));
        return "owner/queues";
    }

    @PostMapping("/{id}/panggil")
    public String panggil(@PathVariable Long id, @RequestParam("tanggal") String tanggal,
                          HttpSession session, RedirectAttributes flash) {
        return aksi(id, tanggal, session, flash, Aksi.PANGGIL);
    }

    @PostMapping("/{id}/mulai")
    public String mulai(@PathVariable Long id, @RequestParam("tanggal") String tanggal,
                        HttpSession session, RedirectAttributes flash) {
        return aksi(id, tanggal, session, flash, Aksi.MULAI);
    }

    @PostMapping("/{id}/selesai")
    public String selesai(@PathVariable Long id, @RequestParam("tanggal") String tanggal,
                          HttpSession session, RedirectAttributes flash) {
        return aksi(id, tanggal, session, flash, Aksi.SELESAI);
    }

    @PostMapping("/{id}/tidak-hadir")
    public String tidakHadir(@PathVariable Long id, @RequestParam("tanggal") String tanggal,
                             HttpSession session, RedirectAttributes flash) {
        return aksi(id, tanggal, session, flash, Aksi.TIDAK_HADIR);
    }

    @PostMapping("/{id}/batalkan")
    public String batalkan(@PathVariable Long id, @RequestParam("tanggal") String tanggal,
                           HttpSession session, RedirectAttributes flash) {
        return aksi(id, tanggal, session, flash, Aksi.BATAL);
    }

    private String aksi(Long id, String tanggal, HttpSession session, RedirectAttributes flash, Aksi aksi) {
        String kembali = "redirect:/owner/queues?tanggal=" + (tanggal == null ? LocalDate.now() : tanggal);
        Business business = activeBusiness.bisnisAktif(session);
        if (business == null) {
            return kembali;
        }
        try {
            Queue queue = switch (aksi) {
                case PANGGIL -> queueService.panggil(id, business);
                case MULAI -> queueService.mulai(id, business);
                case SELESAI -> queueService.selesai(id, business);
                case TIDAK_HADIR -> queueService.tidakHadir(id, business);
                case BATAL -> queueService.batalkan(id, business);
            };
            flash.addFlashAttribute("sukses", pesan(aksi, queue));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return kembali;
    }

    private String pesan(Aksi aksi, Queue queue) {
        return switch (aksi) {
            case PANGGIL -> "Antrean " + queue.getQueueNumber() + " dipanggil.";
            case MULAI -> "Antrean " + queue.getQueueNumber() + " mulai dilayani.";
            case SELESAI -> "Antrean " + queue.getQueueNumber() + " selesai.";
            case TIDAK_HADIR -> "Antrean " + queue.getQueueNumber() + " ditandai tidak hadir.";
            case BATAL -> "Antrean " + queue.getQueueNumber() + " dibatalkan.";
        };
    }

    /** Tanggal GET diparse sebagai yyyy-MM-dd. Nilai kosong atau salah memakai hari ini. */
    private LocalDate parseTanggal(String nilai) {
        if (nilai == null || nilai.isBlank()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(nilai);
        } catch (Exception ex) {
            return LocalDate.now();
        }
    }
}
