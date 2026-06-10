package pl.MiASI.medicalcare.application.port.in;

import pl.MiASI.shared.domain.model.DoctorId;
import pl.MiASI.medicalcare.domain.model.Schedule;
import java.util.Optional;

public interface ScheduleQueryUseCase {
    Optional<Schedule> getScheduleByDoctor(DoctorId doctorId);
}