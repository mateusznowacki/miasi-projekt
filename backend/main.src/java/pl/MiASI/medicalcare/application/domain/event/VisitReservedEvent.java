package pl.MiASI.medicalcare.application.domain.event;

import pl.MiASI.medicalcare.application.domain.model.VisitId;

public record VisitReservedEvent(VisitId visitId) {
}