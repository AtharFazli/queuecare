package com.queuecare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

    /** Halaman yang dilihat saat Spring Security menolak akses (R1). */
    @RequestMapping("/403")
    public String forbidden() {
        return "error/403";
    }
}
