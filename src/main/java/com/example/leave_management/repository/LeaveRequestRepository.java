package com.example.leave_management.repository;

import com.example.leave_management.dto.ManagerLeaveRequestDTO;
import com.example.leave_management.entity.Employee;
import com.example.leave_management.entity.LeaveRequest;
import com.example.leave_management.enumm.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
        List<LeaveRequest> findByEmployeeEmail(String email);

        Optional<LeaveRequest> findByIdAndEmployeeEmail(Long id, String email);

        List<LeaveRequest> findByEmployeeEmployeeCode(String employeeCode);

        Optional<LeaveRequest> findByIdAndEmployeeEmployeeCode(Long id, String employeeCode);

        List<LeaveRequest> findByStatus(LeaveStatus status);

        // @Query("""
        // select lr from LeaveRequest lr
        // where lr.employee.id = :employeeId
        // and lr.status in (:statuses)
        // and lr.startDate <= :endDate
        // and lr.endDate >= :startDate
        // """)
        // List<LeaveRequest> findOverlappingLeaves(
        // Long employeeId,
        // LocalDate startDate,
        // LocalDate endDate,
        // List<LeaveStatus> statuses
        // );

        @Query("""
                            SELECT COUNT(l) > 0
                            FROM LeaveRequest l
                            WHERE l.employee = :employee
                              AND l.status <> :status
                              AND (
                                    :startDate BETWEEN l.startDate AND l.endDate
                                 OR :endDate BETWEEN l.startDate AND l.endDate
                                 OR l.startDate BETWEEN :startDate AND :endDate
                              )
                        """)
        boolean existsOverlappingLeave(
                        @Param("employee") Employee employee,
                        @Param("status") LeaveStatus status,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("""
                            select new com.example.leave_management.dto.ManagerLeaveRequestDTO(
                                lr.id,
                                e.name,
                                lr.type,
                                lr.startDate,
                                lr.endDate,
                                lr.status
                            )
                            from LeaveRequest lr
                            join lr.employee e
                            where lr.status = :status
                        """)
        List<ManagerLeaveRequestDTO> findPendingForManager(
                        @Param("status") LeaveStatus status);

        @Query("""
                        SELECT COALESCE(SUM(l.totalDays), 0)
                        FROM LeaveRequest l
                        WHERE l.employee.id = :empId
                          AND l.status = 'APPROVED'
                          AND (l.type = 'ANNUAL' OR l.type = 'CASUAL')
                        """)
        int sumAnnualDaysByEmployeeId(@Param("empId") Long empId);

}
