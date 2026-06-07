package pl.edu.pwr.MiASI.medical.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;
import java.time.LocalDateTime;

@ValueObject
public record TimeRange(LocalDateTime startTime, LocalDateTime endTime) {
    public TimeRange {
        if (startTime == null || endTime == null || !startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Invalid TimeRange");
        }
    }
}
