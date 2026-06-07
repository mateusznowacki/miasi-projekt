package pl.edu.pwr.MiASI.document.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;

@ValueObject
public record FileReference(String url) {}
