package pl.MiASI.medicalcare.domain.event;

import pl.MiASI.medicalcare.domain.model.VisitId;

// Zdarzenie pochodzące z kontekstu Dokumentacji Medycznej
public record RecordCreatedEvent(VisitId visitId) {}