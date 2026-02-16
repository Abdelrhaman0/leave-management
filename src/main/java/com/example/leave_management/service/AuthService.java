package com.example.leave_management.service;

import com.example.leave_management.entity.Employee;
import com.example.leave_management.entity.RegisterRequest;
import com.example.leave_management.enumm.Role;
import com.example.leave_management.repository.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest request) {

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (employeeRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new RuntimeException("Employee ID already exists");
        }

        Employee employee = new Employee();
        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));

        employee.setHireDate(request.getHireDate());
        employee.setAge(request.getAge());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setJobTitle(request.getJobTitle());


        employee.setRole(Role.EMPLOYEE);
        employee.setAnnualLeaveBalance(21);

        employeeRepository.save(employee);
    }

}
