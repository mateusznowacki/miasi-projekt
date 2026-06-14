package pl.MiASI.medicalcare.application.domain.event;

import pl.MiASI.medicalcare.application.domain.model.ScheduleId;
import pl.MiASI.medicalcare.application.domain.model.SlotId;

import java.util.List;

public record SlotFreedEvent(ScheduleId scheduleId, List<SlotId> slotIds) {
}