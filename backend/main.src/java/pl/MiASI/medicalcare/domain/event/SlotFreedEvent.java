package pl.MiASI.medicalcare.domain.event;

import pl.MiASI.medicalcare.domain.model.ScheduleId;
import pl.MiASI.medicalcare.domain.model.SlotId;

import java.util.List;

public record SlotFreedEvent(ScheduleId scheduleId, List<SlotId> slotIds) {
}