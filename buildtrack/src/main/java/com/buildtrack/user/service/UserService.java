package com.buildtrack.user.service;

import com.buildtrack.user.dto.RegisterForm;
import com.buildtrack.user.entity.*;
import com.buildtrack.user.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterForm form) {
        if (userRepository.existsByEmailIgnoreCase(form.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        Role role = roleRepository.findByName(form.getRole())
            .orElseThrow(() -> new IllegalStateException("Role not seeded: " + form.getRole()));

        User user = User.builder()
            .fullName(form.getFullName().trim())
            .email(form.getEmail().trim().toLowerCase())
            .passwordHash(passwordEncoder.encode(form.getPassword()))
            .role(role)
            .active(true)
            .build();

        return userRepository.save(user);
    }
}
