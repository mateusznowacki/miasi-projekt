package pl.edu.pwr.MiASI.shared.infrastructure;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import pl.edu.pwr.MiASI.shared.domain.DomainEvent;
import pl.edu.pwr.MiASI.shared.domain.DomainEventPublisher;

@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
