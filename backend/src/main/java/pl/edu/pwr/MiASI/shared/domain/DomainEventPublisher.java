package pl.edu.pwr.MiASI.shared.domain;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
