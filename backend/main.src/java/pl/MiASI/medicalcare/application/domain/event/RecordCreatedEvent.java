package pl.MiASI.medicalcare.application.domain.event;

import pl.MiASI.medicalcare.application.domain.model.VisitId;

// Zdarzenie pochodzące z kontekstu Dokumentacji Medycznej
public record RecordCreatedEvent(VisitId visitId) {
}