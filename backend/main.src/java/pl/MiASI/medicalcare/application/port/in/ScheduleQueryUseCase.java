package pl.MiASI.medicalcare.application.port.in;

import pl.MiASI.medicalcare.application.domain.model.Schedule;
import pl.MiASI.shared.application.domain.model.DoctorId;

import java.util.Optional;

public interface ScheduleQueryUseCase {
    Optional<Schedule> getScheduleByDoctor(DoctorId doctorId);

    java.util.List<Schedule> getAllSchedules();
}