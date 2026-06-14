package pl.MiASI.medicalcare.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataScheduleRepository extends JpaRepository<ScheduleJpaEntity, UUID> {
    Optional<ScheduleJpaEntity> findByDoctorId(UUID doctorId);
}
