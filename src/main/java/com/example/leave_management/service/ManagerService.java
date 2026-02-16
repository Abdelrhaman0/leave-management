package com.example.leave_management.service;

import com.example.leave_management.entity.Employee;
import com.example.leave_management.entity.LeaveRequest;
import com.example.leave_management.enumm.LeaveStatus;
import com.example.leave_management.repository.EmployeeRepository;
import com.example.leave_management.repository.LeaveRequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagerService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public ManagerService(LeaveRequestRepository leaveRequestRepository, EmployeeRepository employeeRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public void approveLeave(Long requestId) {

        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Request already processed");
        }

        Employee employee = request.getEmployee();
        employee.setAnnualLeaveBalance(
                employee.getAnnualLeaveBalance() - request.getTotalDays());

        request.setStatus(LeaveStatus.APPROVED);

        employeeRepository.save(employee);
        leaveRequestRepository.save(request);
    }

    @Transactional
    public void rejectLeave(Long requestId) {

        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        request.setStatus(LeaveStatus.REJECTED);
        leaveRequestRepository.save(request);
    }
}
