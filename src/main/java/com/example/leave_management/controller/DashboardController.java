package com.example.leave_management.controller;

import com.example.leave_management.service.EmployeeService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    EmployeeService employeeService ;

    public DashboardController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {

        boolean isManager = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
        if (isManager) {
            return "redirect:/manager/dashboard";
        }

        return "redirect:/employee/dashboard";
    }

}
