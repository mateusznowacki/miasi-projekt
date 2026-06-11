package pl.MiASI.medicalcare.domain.repository;

import pl.MiASI.medicalcare.domain.model.Schedule;
import pl.MiASI.medicalcare.domain.model.ScheduleId;
import pl.MiASI.shared.domain.model.DoctorId;
import java.util.Optional;

public interface ScheduleRepository {
    void save(Schedule schedule);
    Optional<Schedule> findById(ScheduleId scheduleId);
    Optional<Schedule> findByDoctorId(DoctorId doctorId);
    java.util.List<Schedule> findAll();
}