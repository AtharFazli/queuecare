package com.queuecare.controller;

import com.queuecare.entity.Business;
import com.queuecare.service.ActiveBusiness;
import com.queuecare.service.ReportService;
import com.queuecare.util.RentangTanggal;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/** Laporan Owner untuk bisnis aktif, dengan filter rentang tanggal (R11). */
@Controller
@RequestMapping("/owner/reports")
public class OwnerReportController {

    private final ReportService reportService;
    private final ActiveBusiness activeBusiness;

    public OwnerReportController(ReportService reportService, ActiveBusiness activeBusiness) {
        this.reportService = reportService;
        this.activeBusiness = activeBusiness;
    }

    @GetMapping
    public String laporan(@RequestParam(value = "dari", required = false) String dari,
                          @RequestParam(value = "sampai", required = false) String sampai,
                          HttpSession session, Model model) {
        Business business = activeBusiness.bisnisAktif(session);
        if (business == null) {
            return "redirect:/owner/businesses";
        }
        LocalDate hariIni = LocalDate.now();
        LocalDate dariTanggal = RentangTanggal.parse(dari, hariIni.minusDays(29));
        LocalDate sampaiTanggal = RentangTanggal.parse(sampai, hariIni);
        String galat = RentangTanggal.validasi(dariTanggal, sampaiTanggal);
        if (galat != null) {
            model.addAttribute("error", galat);
            dariTanggal = hariIni.minusDays(29);
            sampaiTanggal = hariIni;
        }
        model.addAttribute("bisnis", business);
        model.addAttribute("laporan", reportService.laporanOwner(business, dariTanggal, sampaiTanggal));
        return "owner/reports";
    }
}
