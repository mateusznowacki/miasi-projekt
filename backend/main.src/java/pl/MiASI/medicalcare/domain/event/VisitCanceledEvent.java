package pl.MiASI.medicalcare.domain.event;

import pl.MiASI.medicalcare.domain.model.SlotId;
import pl.MiASI.medicalcare.domain.model.VisitId;

import java.util.List;

public record VisitCanceledEvent(VisitId visitId, List<SlotId> slotIds) {
}