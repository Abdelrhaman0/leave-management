package com.example.leave_management.service;

import com.example.leave_management.entity.Employee;
import com.example.leave_management.entity.LeaveRequest;
import com.example.leave_management.enumm.LeaveStatus;
import com.example.leave_management.enumm.LeaveType;
import com.example.leave_management.repository.LeaveRequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeService employeeService;

    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository, EmployeeService employeeService) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeService = employeeService;
    }

    public void createLeaveRequest(
            String employeeCode,
            LeaveType type,
            LocalDate start,
            LocalDate end,
            String reason,
            MultipartFile attachment) {

        if (end.isBefore(start)) {
            throw new IllegalStateException("End date cannot be before start date");
        }

        Employee employee = employeeService.getByEmployeeCode(employeeCode);

        boolean hasOverlap = leaveRequestRepository.existsOverlappingLeave(
                employee,
                LeaveStatus.REJECTED,
                start,
                end);

        if (hasOverlap) {
            throw new IllegalStateException(
                    "You already have a leave request during this period");
        }

        int days = calculateDays(start, end);

        if (days > employee.getAnnualLeaveBalance()) {
            throw new IllegalStateException("Not enough leave balance");
        }

        LeaveRequest request = new LeaveRequest();
        request.setEmployee(employee);
        request.setType(type);
        request.setStartDate(start);
        request.setEndDate(end);
        request.setReason(reason);
        request.setStatus(LeaveStatus.PENDING);
        request.setTotalDays(days);

        if (attachment != null && !attachment.isEmpty()) {
            try {
                String uploadDir = "uploads/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = UUID.randomUUID().toString() + "_" + attachment.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(attachment.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                request.setAttachmentPath("/uploads/" + fileName);
            } catch (IOException e) {
                throw new RuntimeException("Could not store image. Please try again!", e);
            }
        }

        leaveRequestRepository.save(request);
    }

    public List<LeaveRequest> findByEmployeeEmail(String email) {
        return leaveRequestRepository.findByEmployeeEmail(email);
    }

    private int calculateDays(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date before start date");
        }
        return (int) ChronoUnit.DAYS.between(start, end) + 1;
    }

    @Transactional
    public void cancelLeaveRequest(Long requestId, String code) {

        LeaveRequest request = leaveRequestRepository
                .findByIdAndEmployeeEmployeeCode(requestId, code)
                .orElseThrow(() -> new IllegalStateException("Request not found"));

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Cannot cancel non-pending request");
        }

        leaveRequestRepository.delete(request);
    }

    public List<LeaveRequest> findAllPending() {
        return leaveRequestRepository.findByStatus(LeaveStatus.PENDING);
    }

    @Transactional
    public void approve(Long requestId) {
        approveRequest(requestId);
    }

    @Transactional
    public void reject(Long requestId) {
        rejectRequest(requestId);
    }

    public List<LeaveRequest> getMyRequests(String code) {
        return leaveRequestRepository.findByEmployeeEmployeeCode(code);
    }

    @Transactional
    public void rejectRequest(Long requestId) {

        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalStateException("Request not found"));

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Request already processed");
        }

        request.setStatus(LeaveStatus.REJECTED);
        leaveRequestRepository.save(request);
    }

    public List<LeaveRequest> getPendingRequests() {
        return leaveRequestRepository.findByStatus(LeaveStatus.PENDING);
    }

    @Transactional
    public void approveRequest(Long requestId) {

        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalStateException("Request not found"));

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Request already processed");
        }

        Employee employee = request.getEmployee();
        int days = request.getTotalDays();

        if (employee.getAnnualLeaveBalance() < days) {
            throw new IllegalStateException("Not enough leave balance");
        }

        // 1. Update request status
        request.setStatus(LeaveStatus.APPROVED);
        leaveRequestRepository.saveAndFlush(request);

        // 2. Recalculate employee balance to ensure consistency
        employeeService.recalcBalance(employee, 21);
    }

    // public long countByStatus(String email, LeaveStatus status) {
    // return leaveRequestRepository
    // .findByEmployeeEmail(email)
    // .stream()
    // .filter(r -> r.getStatus() == status)
    // .count();
    // }

}
