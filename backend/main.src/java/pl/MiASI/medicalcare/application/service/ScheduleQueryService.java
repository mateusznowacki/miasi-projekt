package pl.MiASI.medicalcare.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.MiASI.medicalcare.application.port.in.ScheduleQueryUseCase;
import pl.MiASI.medicalcare.application.domain.model.Schedule;
import pl.MiASI.medicalcare.application.port.out.ScheduleRepository;
import pl.MiASI.shared.application.domain.model.DoctorId;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleQueryService implements ScheduleQueryUseCase {
    private final ScheduleRepository scheduleRepository;

    @Override
    public Optional<Schedule> getScheduleByDoctor(DoctorId doctorId) {
        return scheduleRepository.findByDoctorId(doctorId);
    }

    @Override
    public java.util.List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }
}