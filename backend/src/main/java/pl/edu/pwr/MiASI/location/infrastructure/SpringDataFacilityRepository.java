package pl.edu.pwr.MiASI.location.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataFacilityRepository extends JpaRepository<FacilityJpaEntity, UUID> {
}
