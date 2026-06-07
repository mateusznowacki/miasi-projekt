package pl.edu.pwr.MiASI.iam.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;

@ValueObject
public record Consent(String consentType, boolean granted) {
}
