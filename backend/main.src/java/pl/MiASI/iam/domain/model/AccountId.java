package pl.MiASI.iam.domain.model;
import java.util.UUID;
public record AccountId(UUID value) { public AccountId() { this(UUID.randomUUID()); } }