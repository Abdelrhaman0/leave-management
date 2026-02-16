package com.example.leave_management.controller;

import com.example.leave_management.dto.LeaveForm;
import com.example.leave_management.enumm.LeaveType;
import com.example.leave_management.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/leaves")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    // 🟢 Show Request Form
    @GetMapping("/request")
    public String showRequestForm(Model model) {
        model.addAttribute("leaveForm", new LeaveForm());
        model.addAttribute("leaveTypes", LeaveType.values());
        return "leave-request";
    }

    // 🟢 Submit Leave Request
    @PostMapping("/request")
    public String submit(
            @Valid @ModelAttribute("leaveForm") LeaveForm form,
            BindingResult result,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        // 1️⃣ Bean Validation errors
        if (result.hasErrors()) {
            model.addAttribute("leaveTypes", LeaveType.values());
            return "leave-request";
        }

        try {
            leaveRequestService.createLeaveRequest(
                    principal.getName(),
                    form.getType(),
                    form.getStartDate(),
                    form.getEndDate(),
                    form.getReason()
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Leave request submitted successfully"
            );

            return "redirect:/employee/dashboard";

        } catch (IllegalStateException e) {

            // 2️⃣ Business validation errors (overlap, dates, balance...)
            model.addAttribute("leaveTypes", LeaveType.values());
            model.addAttribute("error", e.getMessage());
            return "leave-request";
        }
    }

    // 🟢 My Leaves Page (optional)
    @GetMapping("/my")
    public String myLeaves(Model model, Principal principal) {

        model.addAttribute(
                "requests",
                leaveRequestService.findByEmployeeEmail(principal.getName())
        );

        return "my-leaves";
    }
}
