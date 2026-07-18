package com.buildtrack.project.controller;

import com.buildtrack.project.entity.*;
import com.buildtrack.project.service.ProjectService;
import com.buildtrack.user.entity.RoleName;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','PROJECT_MANAGER')")
public class ProjectController {

    private final ProjectService service;

    // ---------- List ----------
    @GetMapping
    public String list(Model model) {
        model.addAttribute("projects", service.listAll());
        return "project/list";
    }

    // ---------- Create ----------
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("users", service.allUsers());
        model.addAttribute("statuses", ProjectStatus.values());
        return "project/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("project") Project project,
                         BindingResult binding,
                         @RequestParam(required = false) Long pmId,
                         @RequestParam(required = false) Long clientId,
                         Model model,
                         RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("users", service.allUsers());
            model.addAttribute("statuses", ProjectStatus.values());
            return "project/form";
        }
        Project saved = service.save(project, pmId, clientId);
        ra.addFlashAttribute("success", "Project created.");
        return "redirect:/projects/" + saved.getId();
    }

    // ---------- View (detail) ----------
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Project p = service.get(id);
        model.addAttribute("project", p);
        model.addAttribute("newSite", new Site());
        model.addAttribute("newMilestone", new Milestone());
        model.addAttribute("engineers", service.allUsers().stream()
            .filter(u -> u.getRole().getName() == RoleName.SITE_ENGINEER).toList());
        model.addAttribute("siteStatuses", SiteStatus.values());
        model.addAttribute("milestoneStatuses", MilestoneStatus.values());
        return "project/detail";
    }

    // ---------- Edit ----------
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("project", service.get(id));
        model.addAttribute("users", service.allUsers());
        model.addAttribute("statuses", ProjectStatus.values());
        return "project/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("project") Project project,
                         BindingResult binding,
                         @RequestParam(required = false) Long pmId,
                         @RequestParam(required = false) Long clientId,
                         Model model,
                         RedirectAttributes ra) {
        project.setId(id);
        if (binding.hasErrors()) {
            model.addAttribute("users", service.allUsers());
            model.addAttribute("statuses", ProjectStatus.values());
            return "project/form";
        }
        service.save(project, pmId, clientId);
        ra.addFlashAttribute("success", "Project updated.");
        return "redirect:/projects/" + id;
    }

    // ---------- Delete ----------
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        service.delete(id);
        ra.addFlashAttribute("success", "Project deleted.");
        return "redirect:/projects";
    }

    // ---------- Sites (nested) ----------
    @PostMapping("/{id}/sites")
    public String addSite(@PathVariable Long id,
                          @Valid @ModelAttribute("newSite") Site site,
                          @RequestParam(required = false) Long engineerId,
                          RedirectAttributes ra) {
        service.addSite(id, site, engineerId);
        ra.addFlashAttribute("success", "Site added.");
        return "redirect:/projects/" + id + "#sites";
    }

    @PostMapping("/{id}/sites/{siteId}/delete")
    public String deleteSite(@PathVariable Long id, @PathVariable Long siteId, RedirectAttributes ra) {
        service.deleteSite(siteId);
        ra.addFlashAttribute("success", "Site removed.");
        return "redirect:/projects/" + id + "#sites";
    }

    // ---------- Milestones (nested) ----------
    @PostMapping("/{id}/milestones")
    public String addMilestone(@PathVariable Long id,
                               @Valid @ModelAttribute("newMilestone") Milestone m,
                               RedirectAttributes ra) {
        service.addMilestone(id, m);
        ra.addFlashAttribute("success", "Milestone added.");
        return "redirect:/projects/" + id + "#milestones";
    }

    @PostMapping("/{id}/milestones/{msId}/delete")
    public String deleteMilestone(@PathVariable Long id, @PathVariable Long msId, RedirectAttributes ra) {
        service.deleteMilestone(msId);
        ra.addFlashAttribute("success", "Milestone removed.");
        return "redirect:/projects/" + id + "#milestones";
    }
}
