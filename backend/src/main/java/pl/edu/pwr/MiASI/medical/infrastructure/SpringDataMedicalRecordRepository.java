package pl.edu.pwr.MiASI.medical.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataMedicalRecordRepository extends JpaRepository<MedicalRecordJpaEntity, UUID> {
}
