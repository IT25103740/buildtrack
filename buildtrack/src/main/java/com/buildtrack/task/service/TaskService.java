package com.buildtrack.task.service;

import com.buildtrack.common.NotificationService;
import com.buildtrack.project.entity.Site;
import com.buildtrack.project.repository.SiteRepository;
import com.buildtrack.task.entity.*;
import com.buildtrack.task.repository.*;
import com.buildtrack.user.entity.User;
import com.buildtrack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepo;
    private final AttendanceRepository attendanceRepo;
    private final SiteRepository siteRepo;
    private final UserRepository userRepo;
    private final NotificationService notifier;

    // ---- Tasks ----
    public List<Task> forSite(Long siteId) { return taskRepo.findBySiteId(siteId); }
    public List<Task> forAssignee(User u) { return taskRepo.findByAssigneeOrderByDueDateAsc(u); }
    public Task get(Long id) {
        return taskRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    @Transactional
    public Task create(Long siteId, Task task, Long assigneeId) {
        Site site = siteRepo.findById(siteId)
            .orElseThrow(() -> new IllegalArgumentException("Site not found"));
        task.setSite(site);
        if (assigneeId != null) {
            User a = userRepo.findById(assigneeId).orElse(null);
            task.setAssignee(a);
            notifier.notify(a, "New task assigned: " + task.getTitle(), "/worker/tasks");
        }
        return taskRepo.save(task);
    }

    @Transactional
    public Task updateProgress(Long taskId, int progress, TaskStatus status) {
        Task t = get(taskId);
        t.setProgressPct(Math.max(0, Math.min(100, progress)));
        t.setStatus(status != null ? status : t.getStatus());
        if (t.getProgressPct() == 100) t.setStatus(TaskStatus.DONE);
        return taskRepo.save(t);
    }

    @Transactional
    public void delete(Long id) { taskRepo.deleteById(id); }

    // ---- Attendance ----
    public List<Attendance> attendanceForSite(Long siteId, LocalDate date) {
        return attendanceRepo.findBySiteIdAndDate(siteId, date);
    }

    @Transactional
    public Attendance recordAttendance(Long siteId, Long workerId, LocalDate date, AttendanceStatus status) {
        Attendance a = Attendance.builder()
            .site(siteRepo.findById(siteId).orElseThrow())
            .worker(userRepo.findById(workerId).orElseThrow())
            .date(date)
            .status(status)
            .build();
        return attendanceRepo.save(a);
    }
}
