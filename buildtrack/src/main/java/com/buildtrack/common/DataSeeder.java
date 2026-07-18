package com.buildtrack.common;

import com.buildtrack.user.entity.*;
import com.buildtrack.user.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        // 1. Seed roles
        for (RoleName rn : RoleName.values()) {
            roleRepo.findByName(rn).orElseGet(() -> roleRepo.save(Role.builder().name(rn).build()));
        }

        // 2. Seed one demo user per role (password: BuildTrack@123)
        seedUser("Admin User",           "admin@buildtrack.local",    RoleName.ADMIN);
        seedUser("Priya Perera (PM)",    "pm@buildtrack.local",       RoleName.PROJECT_MANAGER);
        seedUser("Ravi Silva (Engineer)","engineer@buildtrack.local", RoleName.SITE_ENGINEER);
        seedUser("Kamal Worker",         "worker@buildtrack.local",   RoleName.WORKER);
        seedUser("ABC Suppliers",        "supplier@buildtrack.local", RoleName.SUPPLIER);
        seedUser("Nimal Client",         "client@buildtrack.local",   RoleName.CLIENT);
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
}
