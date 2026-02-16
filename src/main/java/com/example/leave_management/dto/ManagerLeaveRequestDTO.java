package com.example.leave_management.dto;

import com.example.leave_management.enumm.LeaveStatus;
import com.example.leave_management.enumm.LeaveType;

import java.time.LocalDate;

public record ManagerLeaveRequestDTO(
        Long id,
        String employeeName,
        LeaveType type,
        LocalDate startDate,
        LocalDate endDate,
        LeaveStatus status
) {


}
