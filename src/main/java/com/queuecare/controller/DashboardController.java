package com.queuecare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/admin")
    public String admin() {
        return "admin/index";
    }

    @GetMapping("/owner")
    public String owner() {
        return "owner/index";
    }

    @GetMapping("/customer")
    public String customer() {
        return "customer/index";
    }
}
