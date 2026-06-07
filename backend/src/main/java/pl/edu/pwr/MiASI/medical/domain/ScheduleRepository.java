package pl.edu.pwr.MiASI.medical.domain;
import java.util.Optional;
import pl.edu.pwr.MiASI.staff.domain.DoctorId;

public interface ScheduleRepository {
    void save(Schedule schedule);
    Optional<Schedule> findById(ScheduleId id);
    Optional<Schedule> findByLekarzId(DoctorId lekarzId);
}
