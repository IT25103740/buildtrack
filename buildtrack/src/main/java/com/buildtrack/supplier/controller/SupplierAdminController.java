package com.buildtrack.supplier.controller;

import com.buildtrack.supplier.entity.Supplier;
import com.buildtrack.supplier.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/suppliers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SupplierAdminController {

    private final SupplierService service;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("suppliers", service.all());
        model.addAttribute("newSupplier", new Supplier());
        model.addAttribute("supplierUsers", service.supplierUsers());
        return "supplier/admin-list";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("newSupplier") Supplier supplier,
                         BindingResult binding,
                         @RequestParam(required = false) Long userId,
                         Model model, RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("suppliers", service.all());
            model.addAttribute("supplierUsers", service.supplierUsers());
            return "supplier/admin-list";
        }
        service.save(supplier, userId);
        ra.addFlashAttribute("success", "Supplier saved.");
        return "redirect:/admin/suppliers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        service.delete(id);
        ra.addFlashAttribute("success", "Supplier deleted.");
        return "redirect:/admin/suppliers";
    }
}
