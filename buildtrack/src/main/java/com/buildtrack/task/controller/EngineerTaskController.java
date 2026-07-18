package com.buildtrack.task.controller;

import com.buildtrack.project.entity.Site;
import com.buildtrack.project.repository.SiteRepository;
import com.buildtrack.task.entity.*;
import com.buildtrack.task.service.TaskService;
import com.buildtrack.user.entity.RoleName;
import com.buildtrack.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/engineer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SITE_ENGINEER')")
public class EngineerTaskController {

    private final TaskService taskService;
    private final SiteRepository siteRepo;
    private final UserRepository userRepo;

    @GetMapping("/sites")
    public String siteList(Model model) {
        model.addAttribute("sites", siteRepo.findAll());
        return "task/engineer-sites";
    }

    @GetMapping("/sites/{siteId}")
    public String siteBoard(@PathVariable Long siteId,
                            @RequestParam(required = false) String date,
                            Model model) {
        Site site = siteRepo.findById(siteId).orElseThrow();
        LocalDate day = (date == null || date.isBlank()) ? LocalDate.now() : LocalDate.parse(date);
        model.addAttribute("site", site);
        model.addAttribute("tasks", taskService.forSite(siteId));
        model.addAttribute("workers", userRepo.findAll().stream()
            .filter(u -> u.getRole().getName() == RoleName.WORKER).toList());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("newTask", new Task());
        model.addAttribute("date", day);
        model.addAttribute("attendance", taskService.attendanceForSite(siteId, day));
        model.addAttribute("attStatuses", AttendanceStatus.values());
        return "task/engineer-site-board";
    }

    @PostMapping("/sites/{siteId}/tasks")
    public String createTask(@PathVariable Long siteId,
                             @Valid @ModelAttribute("newTask") Task task,
                             @RequestParam(required = false) Long assigneeId,
                             RedirectAttributes ra) {
        taskService.create(siteId, task, assigneeId);
        ra.addFlashAttribute("success", "Task created.");
        return "redirect:/engineer/sites/" + siteId + "#tasks";
    }

    @PostMapping("/sites/{siteId}/tasks/{taskId}/delete")
    public String deleteTask(@PathVariable Long siteId, @PathVariable Long taskId, RedirectAttributes ra) {
        taskService.delete(taskId);
        ra.addFlashAttribute("success", "Task deleted.");
        return "redirect:/engineer/sites/" + siteId + "#tasks";
    }

    @PostMapping("/sites/{siteId}/attendance")
    public String recordAttendance(@PathVariable Long siteId,
                                   @RequestParam Long workerId,
                                   @RequestParam String date,
                                   @RequestParam AttendanceStatus status,
                                   RedirectAttributes ra) {
        taskService.recordAttendance(siteId, workerId, LocalDate.parse(date), status);
        ra.addFlashAttribute("success", "Attendance recorded.");
        return "redirect:/engineer/sites/" + siteId + "?date=" + date + "#attendance";
    }
}
