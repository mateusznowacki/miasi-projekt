package pl.MiASI.medicalcare.application.port.in;

import pl.MiASI.medicalcare.domain.model.Schedule;
import pl.MiASI.shared.domain.model.DoctorId;

import java.util.Optional;

public interface ScheduleQueryUseCase {
    Optional<Schedule> getScheduleByDoctor(DoctorId doctorId);

    java.util.List<Schedule> getAllSchedules();
}