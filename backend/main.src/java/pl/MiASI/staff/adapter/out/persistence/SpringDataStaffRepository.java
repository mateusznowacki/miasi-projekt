package pl.MiASI.staff.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.MiASI.staff.domain.model.StaffRole;

import java.util.List;
import java.util.UUID;

public interface SpringDataStaffRepository extends JpaRepository<StaffJpaEntity, UUID> {
    List<StaffJpaEntity> findByRole(StaffRole role);
}