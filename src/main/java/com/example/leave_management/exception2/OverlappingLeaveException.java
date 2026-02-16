package com.example.leave_management.exception2;

public class OverlappingLeaveException extends RuntimeException {
    public OverlappingLeaveException(String message) {
        super(message);
    }
}
