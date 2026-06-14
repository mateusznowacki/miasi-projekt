package pl.MiASI.staff.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.MiASI.staff.application.domain.model.StaffRole;

import java.util.List;
import java.util.UUID;

interface SpringDataStaffRepository extends JpaRepository<StaffJpaEntity, UUID> {
    List<StaffJpaEntity> findByRole(StaffRole role);

    boolean existsByPwz(String pwz);

    boolean existsByPwzAndIdNot(String pwz, UUID id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);
}