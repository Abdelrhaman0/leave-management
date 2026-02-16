package com.example.leave_management.controller;

import com.example.leave_management.entity.Employee;
import com.example.leave_management.entity.LeaveRequest;
import com.example.leave_management.enumm.LeaveStatus;
import com.example.leave_management.service.AuthService;
import com.example.leave_management.service.EmployeeService;
import com.example.leave_management.service.LeaveRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeDashboardController {

        private final LeaveRequestService leaveRequestService;
        private final EmployeeService employeeService;
        AuthService authService;

        public EmployeeDashboardController(
                        LeaveRequestService leaveRequestService,
                        EmployeeService employeeService,
                        AuthService authService) {
                this.leaveRequestService = leaveRequestService;
                this.employeeService = employeeService;
                this.authService = authService;
        }

        @GetMapping("/dashboard")
        public String dashboard(Model model, Principal principal) {

                String code = principal.getName();

                Employee employee = employeeService.getByEmployeeCode(code);
                employeeService.recalcBalance(employee);
                List<LeaveRequest> requests = leaveRequestService.getMyRequests(code);

                long pending = requests.stream()
                                .filter(r -> r.getStatus() == LeaveStatus.PENDING)
                                .count();

                long approved = requests.stream()
                                .filter(r -> r.getStatus() == LeaveStatus.APPROVED)
                                .count();

                long rejected = requests.stream()
                                .filter(r -> r.getStatus() == LeaveStatus.REJECTED)
                                .count();

                model.addAttribute("employee", employee);
                model.addAttribute("requests", requests);
                model.addAttribute("pendingCount", pending);
                model.addAttribute("approvedCount", approved);
                model.addAttribute("rejectedCount", rejected);
                model.addAttribute("leaveBalance", employee.getAnnualLeaveBalance());

                return "employee-dashboard";
        }

        @PostMapping("/cancel/{id}")
        public String cancelRequest(@PathVariable("id") Long id, Principal principal) {
                leaveRequestService.cancelLeaveRequest(id, principal.getName());
                return "redirect:/employee/dashboard";
        }

}
