package pl.edu.pwr.MiASI.location.domain;

import pl.edu.pwr.MiASI.shared.domain.ValueObject;

@ValueObject
public record Location(String building, String floor, String number) {
}
