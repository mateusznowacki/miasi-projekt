package pl.edu.pwr.MiASI.medical.infrastructure;

import org.springframework.stereotype.Component;
import pl.edu.pwr.MiASI.medical.domain.*;
import pl.edu.pwr.MiASI.staff.domain.DoctorId;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AppointmentRepositoryAdapter implements AppointmentRepository {
    private final SpringDataAppointmentRepository repository;

    public AppointmentRepositoryAdapter(SpringDataAppointmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Appointment appointment) {
        AppointmentJpaEntity entity = new AppointmentJpaEntity(
            appointment.getId().id(),
            appointment.getPacjentId().id(),
            appointment.getLekarzId().id(),
            appointment.getTyp().name(),
            appointment.getStatus().name(),
            appointment.getSloty().stream().map(SlotId::id).collect(Collectors.toList())
        );
        repository.save(entity);
    }

    @Override
    public Optional<Appointment> findById(AppointmentId id) {
        return repository.findById(id.id()).map(this::toDomain);
    }

    private Appointment toDomain(AppointmentJpaEntity entity) {
        Appointment appointment = new Appointment(
            new AppointmentId(entity.getId()),
            new PatientId(entity.getPacjentId()),
            new DoctorId(entity.getLekarzId()),
            ConsultationType.valueOf(entity.getTyp()),
            entity.getSloty().stream().map(SlotId::new).collect(Collectors.toList())
        );
        if (AppointmentStatus.CANCELLED.name().equals(entity.getStatus())) appointment.cancel();
        if (AppointmentStatus.COMPLETED.name().equals(entity.getStatus())) appointment.complete();
        return appointment;
    }
}
