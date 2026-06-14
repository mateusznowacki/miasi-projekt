package pl.MiASI.staff.infrastructure.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.MiASI.staff.application.domain.model.StaffMember;
import pl.MiASI.staff.application.domain.model.StaffRole;
import pl.MiASI.staff.application.port.out.StaffRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
class StaffRepositoryAdapter implements StaffRepository {

    private final SpringDataStaffRepository repository;

    @Override
    public void save(StaffMember staff) {
        StaffJpaEntity entity = new StaffJpaEntity();
        entity.setId(staff.getId());
        entity.setRole(staff.getRole());
        entity.setFirstName(staff.getFirstName());
        entity.setLastName(staff.getLastName());
        entity.setEmail(staff.getEmail());
        entity.setActive(staff.isActive());
        entity.setSpecialization(staff.getSpecialization());
        entity.setPwz(staff.getPwz());
        entity.setDepartment(staff.getDepartment());
        entity.setPosition(staff.getPosition());
        entity.setWorkSchedule(staff.getWorkSchedule());
        repository.save(entity);
    }

    @Override
    public Optional<StaffMember> findById(UUID id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    @Override
    public List<StaffMember> findAll() {
        return repository.findAll().stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public List<StaffMember> findByRole(StaffRole role) {
        return repository.findByRole(role).stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsByPwz(String pwz) {
        return repository.existsByPwz(pwz);
    }

    @Override
    public boolean existsByPwzAndIdNot(String pwz, UUID id) {
        return repository.existsByPwzAndIdNot(pwz, id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, UUID id) {
        return repository.existsByEmailAndIdNot(email, id);
    }

    private StaffMember mapToDomain(StaffJpaEntity entity) {
        return new StaffMember(
                entity.getId(),
                entity.getRole(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.isActive(),
                entity.getSpecialization(),
                entity.getPwz(),
                entity.getDepartment(),
                entity.getPosition(),
                entity.getWorkSchedule()
        );
    }
}