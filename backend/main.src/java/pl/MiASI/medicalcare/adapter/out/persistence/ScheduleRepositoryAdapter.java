package pl.MiASI.medicalcare.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.MiASI.shared.domain.model.DoctorId;
import pl.MiASI.medicalcare.domain.model.Schedule;
import pl.MiASI.medicalcare.domain.model.ScheduleId;
import pl.MiASI.medicalcare.domain.model.Slot;
import pl.MiASI.medicalcare.domain.model.SlotId;
import pl.MiASI.medicalcare.domain.model.TimeRange;
import pl.MiASI.medicalcare.domain.repository.ScheduleRepository;

import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryAdapter implements ScheduleRepository {

    private final SpringDataScheduleRepository repository;

    @Override
    public void save(Schedule schedule) {
        ScheduleJpaEntity entity = repository.findById(schedule.getScheduleId().value()).orElse(new ScheduleJpaEntity());
        entity.setId(schedule.getScheduleId().value());
        entity.setDoctorId(schedule.getDoctorId().value());
        
        entity.setSlots(schedule.getSlots().stream().map(slot -> {
            SlotJpaEntity slotEntity = new SlotJpaEntity();
            slotEntity.setId(slot.getSlotId().value());
            slotEntity.setStartTime(slot.getTimeRange().startTime());
            slotEntity.setEndTime(slot.getTimeRange().endTime());
            slotEntity.setOffice(slot.getOffice());
            slotEntity.setStatus(slot.getStatus());
            return slotEntity;
        }).collect(Collectors.toList()));

        repository.save(entity);
    }

    @Override
    public Optional<Schedule> findById(ScheduleId scheduleId) {
        return repository.findById(scheduleId.value()).map(this::mapToDomain);
    }

    @Override
    public Optional<Schedule> findByDoctorId(DoctorId doctorId) {
        return repository.findByDoctorId(doctorId.value()).map(this::mapToDomain);
    }

    private Schedule mapToDomain(ScheduleJpaEntity entity) {
        return new Schedule(
                new ScheduleId(entity.getId()),
                new DoctorId(entity.getDoctorId()),
                entity.getSlots().stream().map(slotEntity -> new Slot(
                        new SlotId(slotEntity.getId()),
                        new TimeRange(slotEntity.getStartTime(), slotEntity.getEndTime()),
                        slotEntity.getOffice(),
                        slotEntity.getStatus()
                )).collect(Collectors.toList())
        );
    }
}
