package pl.edu.pwr.MiASI.medical.domain;
import java.util.Optional;

public interface AppointmentRepository {
    void save(Appointment appointment);
    Optional<Appointment> findById(AppointmentId id);
}
