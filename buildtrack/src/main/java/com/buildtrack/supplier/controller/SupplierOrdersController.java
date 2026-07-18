package com.buildtrack.supplier.controller;

import com.buildtrack.supplier.entity.POStatus;
import com.buildtrack.supplier.service.SupplierService;
import com.buildtrack.user.service.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/supplier/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPPLIER')")
public class SupplierOrdersController {

    private final SupplierService service;

    @GetMapping
    public String myOrders(@AuthenticationPrincipal AppUserDetails me, Model model) {
        model.addAttribute("orders", service.forSupplier(me.getUser()));
        return "supplier/supplier-orders";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam POStatus status,
                               RedirectAttributes ra) {
        service.updateStatus(id, status);
        ra.addFlashAttribute("success", "Order updated.");
        return "redirect:/supplier/orders";
    }
}
