package com.example.leave_management.security;

import com.example.leave_management.entity.Employee;
import com.example.leave_management.repository.EmployeeRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public CustomUserDetailsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Employee employee = employeeRepository
                .findByEmployeeCode(username)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found"));

        System.out.println(employee.getPassword());

        return org.springframework.security.core.userdetails.User
                .withUsername(employee.getEmployeeCode())
                .password(employee.getPassword())
                .roles(employee.getRole().name())
                .build();
    }
}
