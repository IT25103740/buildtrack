package com.buildtrack.material.controller;

import com.buildtrack.material.entity.MovementType;
import com.buildtrack.material.service.MaterialService;
import com.buildtrack.project.repository.SiteRepository;
import com.buildtrack.user.service.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/engineer/sites/{siteId}/stock")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SITE_ENGINEER')")
public class EngineerStockController {

    private final MaterialService materialService;
    private final SiteRepository siteRepo;

    @GetMapping
    public String board(@PathVariable Long siteId, Model model) {
        var site = siteRepo.findById(siteId).orElseThrow();
        var materials = materialService.all();

        // Current stock per material at this site
        Map<Long, Long> stockByMaterial = new LinkedHashMap<>();
        for (var m : materials) {
            stockByMaterial.put(m.getId(), materialService.currentStock(m.getId(), siteId));
        }

        model.addAttribute("site", site);
        model.addAttribute("materials", materials);
        model.addAttribute("stockByMaterial", stockByMaterial);
        model.addAttribute("movements", materialService.movementsForSite(siteId));
        model.addAttribute("requests", materialService.forSite(siteId));
        model.addAttribute("types", MovementType.values());
        return "material/engineer-stock";
    }

    @PostMapping("/movement")
    public String recordMovement(@PathVariable Long siteId,
                                 @RequestParam Long materialId,
                                 @RequestParam int qty,
                                 @RequestParam MovementType type,
                                 @RequestParam(required = false) String note,
                                 RedirectAttributes ra) {
        materialService.recordMovement(siteId, materialId, qty, type, note);
        ra.addFlashAttribute("success", "Stock movement recorded.");
        return "redirect:/engineer/sites/" + siteId + "/stock#movements";
    }

    @PostMapping("/request")
    public String requestMaterial(@PathVariable Long siteId,
                                  @RequestParam Long materialId,
                                  @RequestParam int qty,
                                  @AuthenticationPrincipal AppUserDetails me,
                                  RedirectAttributes ra) {
        materialService.createRequest(siteId, materialId, qty, me.getUser());
        ra.addFlashAttribute("success", "Material request submitted.");
        return "redirect:/engineer/sites/" + siteId + "/stock#requests";
    }
}
