package pl.edu.pwr.MiASI.staff.infrastructure;

import org.springframework.stereotype.Component;
import pl.edu.pwr.MiASI.staff.domain.*;
import pl.edu.pwr.MiASI.location.domain.FacilityId;
import pl.edu.pwr.MiASI.location.domain.DepartmentId;
import pl.edu.pwr.MiASI.location.domain.RoomId;
import java.util.Optional;

@Component
public class DoctorRepositoryAdapter implements DoctorRepository {
    private final SpringDataDoctorRepository repository;

    public DoctorRepositoryAdapter(SpringDataDoctorRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Doctor doctor) {
        DoctorJpaEntity entity = new DoctorJpaEntity(
            doctor.getId().id(),
            doctor.getImieNazwisko().imie(),
            doctor.getImieNazwisko().nazwisko(),
            doctor.getSpecjalizacja().nazwa(),
            doctor.getPlacowkaId() != null ? doctor.getPlacowkaId().id() : null,
            doctor.getOddzialId() != null ? doctor.getOddzialId().id() : null,
            doctor.getGabinetId() != null ? doctor.getGabinetId().id() : null
        );
        repository.save(entity);
    }

    @Override
    public Optional<Doctor> findById(DoctorId id) {
        return repository.findById(id.id()).map(this::toDomain);
    }

    private Doctor toDomain(DoctorJpaEntity entity) {
        return new Doctor(
            new DoctorId(entity.getId()),
            new FullName(entity.getImie(), entity.getNazwisko()),
            new Specialization(entity.getSpecjalizacja()),
            entity.getPlacowkaId() != null ? new FacilityId(entity.getPlacowkaId()) : null,
            entity.getOddzialId() != null ? new DepartmentId(entity.getOddzialId()) : null,
            entity.getGabinetId() != null ? new RoomId(entity.getGabinetId()) : null
        );
    }
}
