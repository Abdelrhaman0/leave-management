package com.example.leave_management.service;

import com.example.leave_management.entity.Employee;
import com.example.leave_management.repository.EmployeeRepository;
import com.example.leave_management.repository.LeaveRequestRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository, LeaveRequestRepository leaveRequestRepository,
            PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Employee getByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public Employee getByEmployeeCode(String employeeCode) {
        return employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public Employee getById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> searchEmployees(String query) {
        return employeeRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
    }

    @Transactional
    public void recalcBalance(Employee e) {

        int used = leaveRequestRepository.sumAnnualDaysByEmployeeId(e.getId());

        int totalAllowed = 21;

        int balance = Math.max(totalAllowed - used, 0);

        e.setAnnualLeaveBalance(balance);

        employeeRepository.save(e);
    }

    public void updateProfile(String code, Employee newEmployee) {
        Employee employee = employeeRepository.findByEmployeeCode(code).orElseThrow();

        employee.setPhoneNumber(newEmployee.getPhoneNumber());
        employee.setAge(newEmployee.getAge());

        if (newEmployee.getPassword() != null && !newEmployee.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(newEmployee.getPassword()));
        }

        employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(String employeeCode) {
        Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employeeRepository.delete(employee);
    }

}
