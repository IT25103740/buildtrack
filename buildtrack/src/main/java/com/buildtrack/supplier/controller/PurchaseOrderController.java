package com.buildtrack.supplier.controller;

import com.buildtrack.supplier.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
public class PurchaseOrderController {

    private final SupplierService service;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", service.allPOs());
        model.addAttribute("suppliers", service.all());
        model.addAttribute("requests", service.pendingRequests());
        return "supplier/admin-orders";
    }

    @PostMapping("/raise")
    public String raise(@RequestParam Long requestId,
                        @RequestParam Long supplierId,
                        @RequestParam BigDecimal unitPrice,
                        RedirectAttributes ra) {
        service.raiseFromRequest(requestId, supplierId, unitPrice);
        ra.addFlashAttribute("success", "Purchase order raised.");
        return "redirect:/admin/orders";
    }
}
