package com.example.leave_management.dto;

import com.example.leave_management.enumm.LeaveType;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class LeaveForm {
    @NotNull(message = "نوع الإجازة مطلوب")
    private LeaveType type;

    @NotNull(message = "تاريخ البداية مطلوب")
    private LocalDate startDate;

    @NotNull(message = "تاريخ النهاية مطلوب")
    private LocalDate endDate;

    @NotBlank(message = "سبب الإجازة مطلوب")
    private String reason;

    public @NotNull(message = "نوع الإجازة مطلوب") LeaveType getType() {
        return type;
    }

    public void setType(@NotNull(message = "نوع الإجازة مطلوب") LeaveType type) {
        this.type = type;
    }

    public @NotNull(message = "تاريخ البداية مطلوب") LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull(message = "تاريخ البداية مطلوب") LocalDate startDate) {
        this.startDate = startDate;
    }

    public @NotNull(message = "تاريخ النهاية مطلوب") LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(@NotNull(message = "تاريخ النهاية مطلوب") LocalDate endDate) {
        this.endDate = endDate;
    }

    public @NotBlank(message = "سبب الإجازة مطلوب") String getReason() {
        return reason;
    }

    public void setReason(@NotBlank(message = "سبب الإجازة مطلوب") String reason) {
        this.reason = reason;
    }
}
