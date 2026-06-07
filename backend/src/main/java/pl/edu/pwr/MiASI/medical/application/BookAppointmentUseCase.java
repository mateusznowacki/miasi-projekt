package pl.edu.pwr.MiASI.medical.application;

import org.springframework.stereotype.Service;

import pl.edu.pwr.MiASI.medical.domain.*;
import pl.edu.pwr.MiASI.staff.domain.DoctorId;
import pl.edu.pwr.MiASI.shared.domain.DomainEventPublisher;
import java.util.List;

@Service
public class BookAppointmentUseCase {
    private final ScheduleRepository harmonogramRepository;
    private final AppointmentRepository wizytaRepository;
    private final DomainEventPublisher eventPublisher;

    public BookAppointmentUseCase(ScheduleRepository harmonogramRepository, AppointmentRepository wizytaRepository, DomainEventPublisher eventPublisher) {
        this.harmonogramRepository = harmonogramRepository;
        this.wizytaRepository = wizytaRepository;
        this.eventPublisher = eventPublisher;
    }

    public void execute(PatientId patientId, DoctorId lekarzId, ConsultationType consultationType, List<SlotId> selectedSlotIds) {
        Schedule schedule = harmonogramRepository.findByLekarzId(lekarzId)
            .orElseThrow(() -> new IllegalArgumentException("Schedule nie istnieje dla lekarza: " + lekarzId));

        schedule.bookSlots(selectedSlotIds);
        
        Appointment appointment = new Appointment(AppointmentId.generate(), patientId, lekarzId, consultationType, selectedSlotIds);
        
        harmonogramRepository.save(schedule);
        wizytaRepository.save(appointment);
        
        eventPublisher.publish(new AppointmentBooked(appointment.getId(), patientId, lekarzId, selectedSlotIds));
    }
}
