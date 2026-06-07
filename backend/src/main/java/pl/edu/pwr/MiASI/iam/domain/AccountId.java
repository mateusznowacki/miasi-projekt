package pl.edu.pwr.MiASI.iam.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;
import java.util.UUID;

@ValueObject
public record AccountId(UUID id) {
    public static AccountId generate() {
        return new AccountId(UUID.randomUUID());
    }
    public AccountId {
        if (id == null) throw new IllegalArgumentException("Id cannot be null");
    }
}
