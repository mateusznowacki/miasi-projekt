package pl.edu.pwr.MiASI.medical.application;

import org.springframework.stereotype.Service;

import pl.edu.pwr.MiASI.medical.domain.*;
import pl.edu.pwr.MiASI.shared.domain.DomainEventPublisher;

@Service
public class ReleaseResourcesHandler {
    private final ScheduleRepository harmonogramRepository;
    private final DomainEventPublisher eventPublisher;

    public ReleaseResourcesHandler(ScheduleRepository harmonogramRepository, DomainEventPublisher eventPublisher) {
        this.harmonogramRepository = harmonogramRepository;
        this.eventPublisher = eventPublisher;
    }

    public void handle(AppointmentCancelled event) {
        Schedule schedule = harmonogramRepository.findByLekarzId(event.lekarzId())
            .orElseThrow(() -> new IllegalArgumentException("Brak harmonogramu dla lekarza"));
            
        schedule.releaseSlots(event.releasedSlots());
        harmonogramRepository.save(schedule);
        
        eventPublisher.publish(new SlotReleased(schedule.getId(), event.lekarzId(), event.releasedSlots()));
    }
}
