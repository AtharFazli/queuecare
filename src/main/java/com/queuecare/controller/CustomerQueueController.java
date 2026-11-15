package com.queuecare.controller;

import com.queuecare.dto.QueueStatusDto;
import com.queuecare.entity.Queue;
import com.queuecare.service.QueueService;
import com.queuecare.service.UserContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/** Halaman monitoring antrean Customer (R8). */
@Controller
@RequestMapping("/queue")
public class CustomerQueueController {

    private final QueueService queueService;
    private final UserContext userContext;

    public CustomerQueueController(QueueService queueService, UserContext userContext) {
        this.queueService = queueService;
        this.userContext = userContext;
    }

    @GetMapping("/{id}")
    public String lihat(@PathVariable Long id, Model model) {
        Queue queue = queueService.milikCustomer(id, userContext.currentUser());
        model.addAttribute("antrean", queue);
        model.addAttribute("posisi", queueService.posisi(queue));
        model.addAttribute("keterangan", queueService.keterangan(queue.getStatus()));
        return "queue/show";
    }

    /** Dipakai JavaScript halaman monitoring untuk memuat ulang status dan posisi. */
    @GetMapping("/{id}/status")
    @ResponseBody
    public QueueStatusDto status(@PathVariable Long id) {
        return queueService.statusDto(queueService.milikCustomer(id, userContext.currentUser()));
    }
}
