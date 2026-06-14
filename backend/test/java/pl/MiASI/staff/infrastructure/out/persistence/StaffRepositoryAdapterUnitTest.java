package pl.MiASI.staff.infrastructure.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.MiASI.staff.application.domain.model.StaffMember;
import pl.MiASI.staff.application.domain.model.StaffRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffRepositoryAdapterUnitTest {

    @Mock
    private SpringDataStaffRepository repository;

    @InjectMocks
    private StaffRepositoryAdapter adapter;

    @Test
    @DisplayName("Save should map domain object to entity and save to repository")
    void saveWhenCalledShouldMapAndSaveEntity() {
        // given
        UUID id = UUID.randomUUID();
        StaffMember staffMember = StaffMember.create(id, StaffRole.DOCTOR, "John", "Doe", "john@test.com", "Cardiology", "1234567", "Dept", "Doc", "Shift");
        ArgumentCaptor<StaffJpaEntity> captor = ArgumentCaptor.forClass(StaffJpaEntity.class);

        // when
        adapter.save(staffMember);

        // then
        verify(repository).save(captor.capture());
        StaffJpaEntity savedEntity = captor.getValue();
        assertThat(savedEntity.getId()).isEqualTo(id);
        assertThat(savedEntity.getRole()).isEqualTo(StaffRole.DOCTOR);
        assertThat(savedEntity.getFirstName()).isEqualTo("John");
        assertThat(savedEntity.getLastName()).isEqualTo("Doe");
        assertThat(savedEntity.getEmail()).isEqualTo("john@test.com");
        assertThat(savedEntity.getSpecialization()).isEqualTo("Cardiology");
        assertThat(savedEntity.getPwz()).isEqualTo("1234567");
        assertThat(savedEntity.getDepartment()).isEqualTo("Dept");
        assertThat(savedEntity.getPosition()).isEqualTo("Doc");
        assertThat(savedEntity.getWorkSchedule()).isEqualTo("Shift");
    }

    @Test
    @DisplayName("Find by ID when entity exists should return mapped domain object")
    void findByIdWhenExistsShouldReturnMappedDomainObject() {
        // given
        UUID id = UUID.randomUUID();
        StaffJpaEntity entity = new StaffJpaEntity();
        entity.setId(id);
        entity.setRole(StaffRole.ADMIN_STAFF);
        entity.setFirstName("Anna");
        entity.setLastName("Nowak");
        entity.setEmail("anna@test.com");
        entity.setActive(true);
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        // when
        Optional<StaffMember> result = adapter.findById(id);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getFirstName()).isEqualTo("Anna");
        assertThat(result.get().getRole()).isEqualTo(StaffRole.ADMIN_STAFF);
        assertThat(result.get().isActive()).isTrue();
    }

    @Test
    @DisplayName("Find by ID when entity does not exist should return empty")
    void findByIdWhenNotExistsShouldReturnEmpty() {
        // given
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<StaffMember> result = adapter.findById(id);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Find all should return list of mapped domain objects")
    void findAllWhenCalledShouldReturnMappedList() {
        // given
        StaffJpaEntity entity1 = new StaffJpaEntity();
        entity1.setId(UUID.randomUUID());
        entity1.setRole(StaffRole.DOCTOR);

        StaffJpaEntity entity2 = new StaffJpaEntity();
        entity2.setId(UUID.randomUUID());
        entity2.setRole(StaffRole.ADMIN_STAFF);

        when(repository.findAll()).thenReturn(List.of(entity1, entity2));

        // when
        List<StaffMember> result = adapter.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRole()).isEqualTo(StaffRole.DOCTOR);
        assertThat(result.get(1).getRole()).isEqualTo(StaffRole.ADMIN_STAFF);
    }

    @Test
    @DisplayName("Find by role should return list of mapped domain objects filtered by role")
    void findByRoleWhenCalledShouldReturnMappedList() {
        // given
        StaffJpaEntity entity = new StaffJpaEntity();
        entity.setId(UUID.randomUUID());
        entity.setRole(StaffRole.DOCTOR);

        when(repository.findByRole(StaffRole.DOCTOR)).thenReturn(List.of(entity));

        // when
        List<StaffMember> result = adapter.findByRole(StaffRole.DOCTOR);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRole()).isEqualTo(StaffRole.DOCTOR);
    }

    @Test
    @DisplayName("Delete by ID should delegate to repository")
    void deleteByIdWhenCalledShouldDelegateToRepository() {
        // given
        UUID id = UUID.randomUUID();

        // when
        adapter.deleteById(id);

        // then
        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("Exists by PWZ should return repository result")
    void existsByPwzWhenExistsShouldReturnTrue() {
        // given
        when(repository.existsByPwz("1234567")).thenReturn(true);

        // when
        boolean result = adapter.existsByPwz("1234567");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Exists by PWZ and ID not should return repository result")
    void existsByPwzAndIdNotWhenExistsShouldReturnTrue() {
        // given
        UUID id = UUID.randomUUID();
        when(repository.existsByPwzAndIdNot("1234567", id)).thenReturn(true);

        // when
        boolean result = adapter.existsByPwzAndIdNot("1234567", id);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Exists by email should return repository result")
    void existsByEmailWhenExistsShouldReturnTrue() {
        // given
        when(repository.existsByEmail("test@test.com")).thenReturn(true);

        // when
        boolean result = adapter.existsByEmail("test@test.com");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Exists by email and ID not should return repository result")
    void existsByEmailAndIdNotWhenExistsShouldReturnTrue() {
        // given
        UUID id = UUID.randomUUID();
        when(repository.existsByEmailAndIdNot("test@test.com", id)).thenReturn(true);

        // when
        boolean result = adapter.existsByEmailAndIdNot("test@test.com", id);

        // then
        assertThat(result).isTrue();
    }
}
