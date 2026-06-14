package pl.MiASI.medicalcare.application.port.out;

import pl.MiASI.medicalcare.application.domain.model.Schedule;
import pl.MiASI.medicalcare.application.domain.model.ScheduleId;
import pl.MiASI.shared.application.domain.model.DoctorId;

import java.util.Optional;

public interface ScheduleRepository {
    void save(Schedule schedule);

    Optional<Schedule> findById(ScheduleId scheduleId);

    Optional<Schedule> findByDoctorId(DoctorId doctorId);

    java.util.List<Schedule> findAll();
}