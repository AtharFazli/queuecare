package com.queuecare.controller;

import com.queuecare.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UserRepository userRepository;

    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/admin")
    public String admin(Model model, Authentication authentication) {
        sapa(model, authentication);
        return "admin/index";
    }

    @GetMapping("/owner")
    public String owner(Model model, Authentication authentication) {
        sapa(model, authentication);
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
