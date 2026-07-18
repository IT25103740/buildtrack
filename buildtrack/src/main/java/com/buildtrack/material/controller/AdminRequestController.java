package com.buildtrack.material.controller;

import com.buildtrack.material.entity.RequestStatus;
import com.buildtrack.material.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/requests")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
public class AdminRequestController {

    private final MaterialService service;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("requests", service.allRequests());
        return "material/admin-requests";
    }

    @PostMapping("/{id}/status")
    public String update(@PathVariable Long id, @RequestParam RequestStatus status, RedirectAttributes ra) {
        service.updateRequestStatus(id, status);
        ra.addFlashAttribute("success", "Request updated.");
        return "redirect:/admin/requests";
    }
}
