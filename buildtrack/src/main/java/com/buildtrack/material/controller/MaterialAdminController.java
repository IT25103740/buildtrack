package com.buildtrack.material.controller;

import com.buildtrack.material.entity.Material;
import com.buildtrack.material.service.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/materials")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MaterialAdminController {

    private final MaterialService service;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("materials", service.all());
        model.addAttribute("newMaterial", new Material());
        return "material/admin-list";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("newMaterial") Material material,
                         BindingResult binding, Model model, RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("materials", service.all());
            return "material/admin-list";
        }
        service.save(material);
        ra.addFlashAttribute("success", "Material added.");
        return "redirect:/admin/materials";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        service.delete(id);
        ra.addFlashAttribute("success", "Material deleted.");
        return "redirect:/admin/materials";
    }
}
