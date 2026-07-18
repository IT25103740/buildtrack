package com.buildtrack.common;

import com.buildtrack.project.entity.*;
import com.buildtrack.project.repository.*;
import com.buildtrack.task.entity.*;
import com.buildtrack.task.repository.*;
import com.buildtrack.user.entity.*;
import com.buildtrack.user.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final ProjectRepository projectRepo;
    private final SiteRepository siteRepo;
    private final MilestoneRepository milestoneRepo;
    private final TaskRepository taskRepo;
    private final AttendanceRepository attendanceRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        seedRolesAndUsers();
        seedDemoProject();
    }

    // ---------- Roles + users ----------
    private void seedRolesAndUsers() {
        for (RoleName rn : RoleName.values()) {
            roleRepo.findByName(rn).orElseGet(() -> roleRepo.save(Role.builder().name(rn).build()));
        }
        seedUser("Admin User",            "admin@buildtrack.local",    RoleName.ADMIN);
        seedUser("Priya Perera (PM)",     "pm@buildtrack.local",       RoleName.PROJECT_MANAGER);
        seedUser("Ravi Silva (Engineer)", "engineer@buildtrack.local", RoleName.SITE_ENGINEER);
        seedUser("Kamal Worker",          "worker@buildtrack.local",   RoleName.WORKER);
        seedUser("ABC Suppliers",         "supplier@buildtrack.local", RoleName.SUPPLIER);
        seedUser("Nimal Client",          "client@buildtrack.local",   RoleName.CLIENT);
    }

    private void seedUser(String name, String email, RoleName rn) {
        if (userRepo.existsByEmailIgnoreCase(email)) return;
        Role role = roleRepo.findByName(rn).orElseThrow();
        userRepo.save(User.builder()
            .fullName(name)
            .email(email)
            .passwordHash(encoder.encode("BuildTrack@123"))
            .role(role)
            .active(true)
            .build());
    }

    // ---------- Demo project + sites + milestone + task ----------
    private void seedDemoProject() {
        if (projectRepo.count() > 0) return;   // seed only once

        User pm       = userRepo.findByEmailIgnoreCase("pm@buildtrack.local").orElseThrow();
        User engineer = userRepo.findByEmailIgnoreCase("engineer@buildtrack.local").orElseThrow();
        User worker   = userRepo.findByEmailIgnoreCase("worker@buildtrack.local").orElseThrow();
        User client   = userRepo.findByEmailIgnoreCase("client@buildtrack.local").orElseThrow();

        Project p = projectRepo.save(Project.builder()
            .name("Kandy Highway Extension")
            .description("Extension of Kandy expressway with two new sections and bridge upgrades.")
            .projectManager(pm)
            .client(client)
            .budget(new BigDecimal("50000000.00"))
            .startDate(LocalDate.now().minusDays(7))
            .endDate(LocalDate.now().plusMonths(6))
            .status(ProjectStatus.IN_PROGRESS)
            .build());

        Site sA = siteRepo.save(Site.builder()
            .name("Section A")
            .location("Kandy Road, KM 12")
            .project(p)
            .siteEngineer(engineer)
            .status(SiteStatus.ACTIVE)
            .build());

        siteRepo.save(Site.builder()
            .name("Section B")
            .location("Kandy Road, KM 18")
            .project(p)
            .siteEngineer(engineer)
            .status(SiteStatus.ACTIVE)
            .build());

        milestoneRepo.save(Milestone.builder()
            .name("Foundation complete")
            .dueDate(LocalDate.now().plusMonths(1))
            .weightPct(30)
            .status(MilestoneStatus.IN_PROGRESS)
            .project(p)
            .build());

        milestoneRepo.save(Milestone.builder()
            .name("Structure complete")
            .dueDate(LocalDate.now().plusMonths(3))
            .weightPct(40)
            .status(MilestoneStatus.PENDING)
            .project(p)
            .build());

        taskRepo.save(Task.builder()
            .title("Excavate foundation")
            .description("Excavate section A foundation to 2m depth.")
            .site(sA)
            .assignee(worker)
            .dueDate(LocalDate.now().plusDays(3))
            .progressPct(20)
            .status(TaskStatus.IN_PROGRESS)
            .build());

        attendanceRepo.save(Attendance.builder()
            .worker(worker)
            .site(sA)
            .date(LocalDate.now())
            .status(AttendanceStatus.PRESENT)
            .build());
    }
}
