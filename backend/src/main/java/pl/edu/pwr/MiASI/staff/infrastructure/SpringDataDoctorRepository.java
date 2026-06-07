package pl.edu.pwr.MiASI.staff.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataDoctorRepository extends JpaRepository<DoctorJpaEntity, UUID> {
}
