package pl.edu.pwr.MiASI.location.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;

@ValueObject
public record Address(String street, String city, String zipCode) {
}
