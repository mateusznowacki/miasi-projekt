package pl.MiASI.medicalcare.domain.event;

import pl.MiASI.medicalcare.domain.model.VisitId;

public record VisitCompletedEvent(VisitId visitId) {}