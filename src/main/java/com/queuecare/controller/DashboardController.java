package com.queuecare.controller;

import com.queuecare.entity.Business;
import com.queuecare.repository.UserRepository;
import com.queuecare.service.ActiveBusiness;
import com.queuecare.service.ReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UserRepository userRepository;
    private final ReportService reportService;
    private final ActiveBusiness activeBusiness;

    public DashboardController(UserRepository userRepository,
                               ReportService reportService,
                               ActiveBusiness activeBusiness) {
        this.userRepository = userRepository;
        this.reportService = reportService;
        this.activeBusiness = activeBusiness;
    }

    @GetMapping("/admin")
    public String admin(Model model, Authentication authentication) {
        sapa(model, authentication);
        model.addAttribute("stats", reportService.statistikAdmin());
        return "admin/index";
    }

    @GetMapping("/owner")
    public String owner(Model model, Authentication authentication, HttpSession session) {
        sapa(model, authentication);
        Business bisnis = activeBusiness.bisnisAktif(session);
        // Angka dashboard Owner hanya untuk bisnis aktif yang sudah dicek kepemilikannya (KNF-05).
        model.addAttribute("stats", bisnis == null ? null : reportService.statistikOwner(bisnis));
        return "owner/index";
    }

    @GetMapping("/customer")
    public String customer(Model model, Authentication authentication) {
        sapa(model, authentication);
        return "customer/index";
    }

    private void sapa(Model model, Authentication authentication) {
        userRepository.findByEmail(authentication.getName())
                .ifPresent(user -> model.addAttribute("nama", user.getName()));
    }
}
