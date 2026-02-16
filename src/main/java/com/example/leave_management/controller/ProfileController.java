package com.example.leave_management.controller;

import com.example.leave_management.entity.Employee;
import com.example.leave_management.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final EmployeeService userService;

    public ProfileController(EmployeeService userService) {
        this.userService = userService;
    }

    @GetMapping("/edit")
    public String editProfile(Model model, Principal principal) {
        Employee user = userService.getByEmployeeCode(principal.getName());
        model.addAttribute("user", user);
        return "profile-edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@ModelAttribute Employee user,
            Principal principal) {

        userService.updateProfile(principal.getName(), user);
        return "redirect:/dashboard?updated";
    }
}
