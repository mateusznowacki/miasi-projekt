package pl.MiASI.medicalcare.infrastructure.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.MiASI.medicalcare.application.domain.model.*;
import pl.MiASI.medicalcare.application.port.out.ScheduleRepository;
import pl.MiASI.shared.application.domain.model.DoctorId;

import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
class ScheduleRepositoryAdapter implements ScheduleRepository {

    private final SpringDataScheduleRepository repository;

    @Override
    public void save(Schedule schedule) {
        ScheduleJpaEntity entity = repository.findById(schedule.scheduleId().value()).orElse(new ScheduleJpaEntity());
        entity.setId(schedule.scheduleId().value());
        entity.setDoctorId(schedule.doctorId().value());

        if (entity.getSlots() == null) {
            entity.setSlots(new java.util.ArrayList<>());
        }
        entity.getSlots().clear();
        entity.getSlots().addAll(schedule.slots().stream().map(slot -> {
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

    @Override
    public java.util.List<Schedule> findAll() {
        return repository.findAll().stream().map(this::mapToDomain).collect(Collectors.toList());
    }
}
