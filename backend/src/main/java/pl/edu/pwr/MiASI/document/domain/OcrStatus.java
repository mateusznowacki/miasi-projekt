package pl.edu.pwr.MiASI.document.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;

@ValueObject
public enum OcrStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    ERROR
}
