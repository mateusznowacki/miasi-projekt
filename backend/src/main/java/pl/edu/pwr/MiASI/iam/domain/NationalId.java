package pl.edu.pwr.MiASI.iam.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;

@ValueObject
public record NationalId(String value) {
    public NationalId {
        if (value == null || value.length() != 11) {
            throw new IllegalArgumentException("Invalid NationalId");
        }
    }
}
