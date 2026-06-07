package pl.edu.pwr.MiASI.medical.infrastructure;

import org.springframework.stereotype.Component;
import pl.edu.pwr.MiASI.medical.domain.*;
import pl.edu.pwr.MiASI.staff.domain.DoctorId;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ScheduleRepositoryAdapter implements ScheduleRepository {
    private final SpringDataScheduleRepository repository;

    public ScheduleRepositoryAdapter(SpringDataScheduleRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Schedule schedule) {
        var slots = schedule.getSloty().stream().map(s -> 
            new SlotJpaEmbeddable(s.getId().id(), s.getOkres().startTime(), s.getOkres().endTime(), s.getStan().name())
        ).collect(Collectors.toList());

        ScheduleJpaEntity entity = new ScheduleJpaEntity(
            schedule.getId().id(),
            schedule.getLekarzId().id(),
            slots
        );
        repository.save(entity);
    }

    @Override
    public Optional<Schedule> findById(ScheduleId id) {
        return repository.findById(id.id()).map(this::toDomain);
    }

    @Override
    public Optional<Schedule> findByLekarzId(DoctorId lekarzId) {
        return repository.findByLekarzId(lekarzId.id()).map(this::toDomain);
    }

    private Schedule toDomain(ScheduleJpaEntity entity) {
        Schedule schedule = new Schedule(
            new ScheduleId(entity.getId()),
            new DoctorId(entity.getLekarzId())
        );
        
        var slots = entity.getSloty().stream().map(s -> 
            new Slot(new SlotId(s.getSlotId()), new TimeRange(s.getOdKiedy(), s.getDoKiedy()), SlotStatus.valueOf(s.getStan()))
        ).collect(Collectors.toList());
        
        schedule.addSlots(slots);
        return schedule;
    }
}
