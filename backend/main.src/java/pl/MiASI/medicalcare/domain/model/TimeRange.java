package pl.MiASI.medicalcare.domain.model;

import java.time.LocalDateTime;

public record TimeRange(LocalDateTime startTime, LocalDateTime endTime) {
    public TimeRange {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time cannot be null");
        }
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }
}