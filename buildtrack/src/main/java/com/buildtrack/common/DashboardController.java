package com.buildtrack.common;

import com.buildtrack.user.service.AppUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    /** Central hub — routes each role to their dashboard. */
    @GetMapping("/dashboard")
    public String dashboardRouter(@AuthenticationPrincipal AppUserDetails principal) {
        return switch (principal.getUser().getRole().getName()) {
            case ADMIN            -> "redirect:/admin";
            case PROJECT_MANAGER  -> "redirect:/pm";
            case SITE_ENGINEER    -> "redirect:/engineer";
            case WORKER           -> "redirect:/worker";
            case SUPPLIER         -> "redirect:/supplier";
            case CLIENT           -> "redirect:/client";
        };
    }

    @GetMapping("/admin")    public String admin(Model m)    { m.addAttribute("role", "Admin");           return "dashboard/generic"; }
    @GetMapping("/pm")       public String pm(Model m)       { m.addAttribute("role", "Project Manager"); return "dashboard/generic"; }
    @GetMapping("/engineer") public String engineer(Model m) { m.addAttribute("role", "Site Engineer");   return "dashboard/generic"; }
    @GetMapping("/worker")   public String worker(Model m)   { m.addAttribute("role", "Worker");          return "dashboard/generic"; }
    @GetMapping("/supplier") public String supplier(Model m) { m.addAttribute("role", "Supplier");        return "dashboard/generic"; }
    @GetMapping("/client")   public String client(Model m)   { m.addAttribute("role", "Client");          return "dashboard/generic"; }
}
