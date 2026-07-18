package com.buildtrack.user.controller;

import com.buildtrack.user.dto.RegisterForm;
import com.buildtrack.user.entity.RoleName;
import com.buildtrack.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegisterForm());
        model.addAttribute("roles", RoleName.values());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult binding,
                           Model model,
                           RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("roles", RoleName.values());
            return "auth/register";
        }
        try {
            userService.register(form);
        } catch (IllegalArgumentException ex) {
            binding.rejectValue("email", "duplicate", ex.getMessage());
            model.addAttribute("roles", RoleName.values());
            return "auth/register";
        }
        ra.addFlashAttribute("success", "Account created. Please log in.");
        return "redirect:/login";
    }

    @GetMapping("/403")
    public String forbidden() { return "auth/403"; }
}
