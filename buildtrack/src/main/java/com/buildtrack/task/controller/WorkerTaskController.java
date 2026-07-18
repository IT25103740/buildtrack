package com.buildtrack.task.controller;

import com.buildtrack.task.entity.TaskStatus;
import com.buildtrack.task.service.TaskService;
import com.buildtrack.user.service.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/worker")
@RequiredArgsConstructor
@PreAuthorize("hasRole('WORKER')")
public class WorkerTaskController {

    private final TaskService taskService;

    @GetMapping("/tasks")
    public String myTasks(@AuthenticationPrincipal AppUserDetails me, Model model) {
        model.addAttribute("tasks", taskService.forAssignee(me.getUser()));
        model.addAttribute("statuses", TaskStatus.values());
        return "task/worker-tasks";
    }

    @PostMapping("/tasks/{id}/progress")
    public String updateProgress(@PathVariable Long id,
                                 @RequestParam int progress,
                                 @RequestParam TaskStatus status,
                                 RedirectAttributes ra) {
        taskService.updateProgress(id, progress, status);
        ra.addFlashAttribute("success", "Progress updated.");
        return "redirect:/worker/tasks";
    }
}
