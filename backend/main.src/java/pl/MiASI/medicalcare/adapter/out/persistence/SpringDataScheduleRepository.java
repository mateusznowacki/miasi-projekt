package pl.MiASI.medicalcare.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataScheduleRepository extends JpaRepository<ScheduleJpaEntity, UUID> {
    Optional<ScheduleJpaEntity> findByDoctorId(UUID doctorId);
}
