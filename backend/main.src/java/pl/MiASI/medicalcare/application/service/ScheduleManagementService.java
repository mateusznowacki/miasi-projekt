package pl.MiASI.medicalcare.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.MiASI.medicalcare.application.domain.event.SlotFreedEvent;
import pl.MiASI.medicalcare.application.domain.model.Schedule;
import pl.MiASI.medicalcare.application.domain.model.SlotId;
import pl.MiASI.medicalcare.application.domain.model.TimeRange;
import pl.MiASI.medicalcare.application.port.in.AddSlotCommand;
import pl.MiASI.medicalcare.application.port.in.ScheduleManagementUseCase;
import pl.MiASI.medicalcare.application.port.out.ScheduleRepository;
import pl.MiASI.shared.application.domain.model.DoctorId;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleManagementService implements ScheduleManagementUseCase {

    private final ScheduleRepository scheduleRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void addTimeSlots(DoctorId doctorId, List<AddSlotCommand> commands) {
        Schedule schedule = scheduleRepository.findByDoctorId(doctorId)
                .orElseGet(() -> Schedule.create(doctorId));

        schedule.addTimeSlots(commands);
        scheduleRepository.save(schedule);
    }

    @Override
    @Transactional
    public void freeSlots(DoctorId doctorId, List<SlotId> slotIds) {
        Schedule schedule = scheduleRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        schedule.freeSlots(slotIds);
        scheduleRepository.save(schedule);

        eventPublisher.publishEvent(new SlotFreedEvent(schedule.scheduleId(), slotIds));
    }

    @Override
    @Transactional
    public void updateSlot(DoctorId doctorId, SlotId slotId, TimeRange newTimeRange, String newOffice) {
        Schedule schedule = scheduleRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
        schedule.updateSlot(slotId, newTimeRange, newOffice);
        scheduleRepository.save(schedule);
    }

    @Override
    @Transactional
    public void removeSlot(DoctorId doctorId, SlotId slotId) {
        Schedule schedule = scheduleRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
        schedule.removeSlot(slotId);
        scheduleRepository.save(schedule);
    }
}