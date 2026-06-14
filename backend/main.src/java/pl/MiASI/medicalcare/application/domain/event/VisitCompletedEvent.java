package pl.MiASI.medicalcare.application.domain.event;

import pl.MiASI.medicalcare.application.domain.model.VisitId;

public record VisitCompletedEvent(VisitId visitId) {
}