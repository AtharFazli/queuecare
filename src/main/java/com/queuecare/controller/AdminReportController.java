package com.queuecare.controller;

import com.queuecare.service.ReportService;
import com.queuecare.util.RentangTanggal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/** Laporan Admin dengan filter rentang tanggal (R12). */
@Controller
@RequestMapping("/admin/reports")
public class AdminReportController {

    private final ReportService reportService;

    public AdminReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public String laporan(@RequestParam(value = "dari", required = false) String dari,
                          @RequestParam(value = "sampai", required = false) String sampai,
                          Model model) {
        LocalDate dariTanggal = RentangTanggal.parse(dari, null);
        LocalDate sampaiTanggal = RentangTanggal.parse(sampai, null);
        String galat = RentangTanggal.validasi(dariTanggal, sampaiTanggal);
        if (galat != null) {
            model.addAttribute("error", galat);
            dariTanggal = null;
            sampaiTanggal = null;
        }
        model.addAttribute("laporan", reportService.laporanAdmin(dariTanggal, sampaiTanggal));
        return "admin/reports";
    }
}
