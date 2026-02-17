package com.example.leave_management.enumm;


public enum LeaveType {
    SICK("مرضية"),
    CASUAL("اعتيادية"),
    ANNUAL("سنوية"),
    EMERGENCY("طارئة");

    private final String displayName;

    LeaveType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
