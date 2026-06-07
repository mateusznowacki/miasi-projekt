package pl.edu.pwr.MiASI.medical.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

public interface SpringDataScheduleRepository extends JpaRepository<ScheduleJpaEntity, UUID> {
    Optional<ScheduleJpaEntity> findByLekarzId(UUID lekarzId);
}
