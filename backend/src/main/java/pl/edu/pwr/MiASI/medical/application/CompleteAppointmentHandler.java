package pl.edu.pwr.MiASI.medical.application;

import org.springframework.stereotype.Service;

import pl.edu.pwr.MiASI.medical.domain.*;
import pl.edu.pwr.MiASI.shared.domain.DomainEventPublisher;

@Service
public class CompleteAppointmentHandler {
    private final AppointmentRepository wizytaRepository;
    private final DomainEventPublisher eventPublisher;

    public CompleteAppointmentHandler(AppointmentRepository wizytaRepository, DomainEventPublisher eventPublisher) {
        this.wizytaRepository = wizytaRepository;
        this.eventPublisher = eventPublisher;
    }

    public void handle(MedicalRecordCreated event) {
        Appointment appointment = wizytaRepository.findById(event.wizytaId())
            .orElseThrow(() -> new IllegalArgumentException("Appointment nie istnieje"));
            
        appointment.complete();
        wizytaRepository.save(appointment);
        
        eventPublisher.publish(new AppointmentCompleted(appointment.getId()));
    }
}
