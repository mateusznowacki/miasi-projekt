package pl.MiASI.staff.domain.repository;

import pl.MiASI.staff.domain.model.StaffMember;
import pl.MiASI.staff.domain.model.StaffRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffRepository {
    void save(StaffMember staff);

    Optional<StaffMember> findById(UUID id);

    List<StaffMember> findAll();

    List<StaffMember> findByRole(StaffRole role);

    void deleteById(UUID id);

    boolean existsByPwz(String pwz);

    boolean existsByPwzAndIdNot(String pwz, UUID id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);
}