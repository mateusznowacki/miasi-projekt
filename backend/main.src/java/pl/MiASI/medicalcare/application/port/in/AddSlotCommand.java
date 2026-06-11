package pl.MiASI.medicalcare.application.port.in;

import pl.MiASI.medicalcare.domain.model.TimeRange;

public record AddSlotCommand(TimeRange timeRange, String office) {
}
