package pl.edu.pwr.MiASI.iam.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;

@ValueObject
public record Email(String value) {
    public Email {
        if (value == null || !value.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}
