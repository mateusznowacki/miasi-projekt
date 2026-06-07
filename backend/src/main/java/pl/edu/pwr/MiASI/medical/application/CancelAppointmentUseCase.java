package pl.edu.pwr.MiASI.medical.application;

import org.springframework.stereotype.Service;

import pl.edu.pwr.MiASI.medical.domain.*;
import pl.edu.pwr.MiASI.shared.domain.DomainEventPublisher;

@Service
public class CancelAppointmentUseCase {
    private final AppointmentRepository wizytaRepository;
    private final DomainEventPublisher eventPublisher;

    public CancelAppointmentUseCase(AppointmentRepository wizytaRepository, DomainEventPublisher eventPublisher) {
        this.wizytaRepository = wizytaRepository;
        this.eventPublisher = eventPublisher;
    }

    public void execute(AppointmentId wizytaId) {
        Appointment appointment = wizytaRepository.findById(wizytaId)
            .orElseThrow(() -> new IllegalArgumentException("Appointment nie istnieje: " + wizytaId));

        appointment.cancel();
        
        wizytaRepository.save(appointment);
        
        eventPublisher.publish(new AppointmentCancelled(appointment.getId(), appointment.getLekarzId(), appointment.getSloty()));
    }
}
