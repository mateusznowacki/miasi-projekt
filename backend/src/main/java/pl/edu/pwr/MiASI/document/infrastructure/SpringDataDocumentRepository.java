package pl.edu.pwr.MiASI.document.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataDocumentRepository extends JpaRepository<DocumentJpaEntity, UUID> {
}
