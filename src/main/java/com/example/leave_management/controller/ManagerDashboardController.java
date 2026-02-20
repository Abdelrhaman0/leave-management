package com.example.leave_management.controller;

import com.example.leave_management.entity.Employee;
import com.example.leave_management.entity.RegisterRequest;
import com.example.leave_management.service.AuthService;
import com.example.leave_management.service.EmployeeService;
import com.example.leave_management.service.LeaveRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manager")
public class  ManagerDashboardController {

    private final LeaveRequestService leaveRequestService;

    private final EmployeeService employeeService;

    private final AuthService authService;

    public ManagerDashboardController(
            LeaveRequestService leaveRequestService,
            EmployeeService employeeService,
            AuthService authService) {
        this.leaveRequestService = leaveRequestService;
        this.employeeService = employeeService;
        this.authService = authService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, java.security.Principal principal) {
        String code = principal.getName();
        Employee manager = employeeService.getByEmployeeCode(code);

        model.addAttribute("manager", manager);
        model.addAttribute("requests",
                leaveRequestService.getPendingRequests());

        return "manager-dashboard";
    }

    @PostMapping("/approve/{id}")

    public String approve(@PathVariable("id") Long id) {

        leaveRequestService.approveRequest(id);

        return "redirect:/manager/dashboard";
    }

    @PostMapping("/reject/{id}")
    public String reject(@PathVariable("id") Long id) {

        leaveRequestService.rejectRequest(id);

        return "redirect:/manager/dashboard";
    }

    @GetMapping("/employees")
    public String viewEmployees(
            @RequestParam(name = "query", required = false) String query,
            Model model) {

        if (query != null && !query.isEmpty()) {
            model.addAttribute("employees",
                    employeeService.searchEmployees(query));
        } else {
            model.addAttribute("employees",
                    employeeService.getAllEmployees());
        }

        model.addAttribute("query", query);

        return "manager-employees";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @ModelAttribute("registerRequest") RegisterRequest request,
            Model model) {
        try {
            authService.register(request);
            return "redirect:/manager/employees";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
    }

    @PostMapping("/delete/{code}")
    public String deleteEmployee(@PathVariable("code") String employeeCode) {
        employeeService.deleteEmployee(employeeCode);
        return "redirect:/manager/employees";
    }

}
