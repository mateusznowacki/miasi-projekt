package pl.MiASI.medicalcare.application.domain.event;

import pl.MiASI.medicalcare.application.domain.model.SlotId;
import pl.MiASI.medicalcare.application.domain.model.VisitId;

import java.util.List;

public record VisitCanceledEvent(VisitId visitId, List<SlotId> slotIds) {
}