package pl.MiASI.medicalcare.application.port.in;

import pl.MiASI.medicalcare.application.domain.model.TimeRange;

public record AddSlotCommand(TimeRange timeRange, String office) {
}
