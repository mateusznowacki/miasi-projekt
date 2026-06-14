package pl.MiASI.staff.infrastructure.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.MiASI.staff.application.domain.model.StaffMember;
import pl.MiASI.staff.application.domain.model.StaffRole;
import pl.MiASI.staff.application.port.in.StaffUseCase;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorControllerUnitTest {

    @Mock
    private StaffUseCase staffUseCase;

    @InjectMocks
    private DoctorController doctorController;

    @Test
    @DisplayName("List doctors when no filters provided should return all doctors")
    void listDoctorsWhenNoFiltersShouldReturnAllDoctors() {
        // given
        StaffMember doc1 = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "Jan", "Kowalski", "jan@test.com", "Cardiology", "1111111", null, null, null);
        StaffMember doc2 = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "Anna", "Nowak", "anna@test.com", "Neurology", "2222222", null, null, null);
        when(staffUseCase.getStaffByRole(StaffRole.DOCTOR)).thenReturn(List.of(doc1, doc2));

        // when
        ResponseEntity<List<StaffDto>> response = doctorController.listDoctors(null, null);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).firstName()).isEqualTo("Jan");
        assertThat(response.getBody().get(1).firstName()).isEqualTo("Anna");
    }

    @Test
    @DisplayName("List doctors when specialization filter provided should return matching doctors")
    void listDoctorsWhenSpecializationFilterShouldReturnMatchingDoctors() {
        // given
        StaffMember doc1 = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "Jan", "Kowalski", "jan@test.com", "Cardiology", "1111111", null, null, null);
        StaffMember doc2 = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "Anna", "Nowak", "anna@test.com", "Neurology", "2222222", null, null, null);
        when(staffUseCase.getStaffByRole(StaffRole.DOCTOR)).thenReturn(List.of(doc1, doc2));

        // when
        ResponseEntity<List<StaffDto>> response = doctorController.listDoctors("Neurology", null);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).specialization()).isEqualTo("Neurology");
    }

    @Test
    @DisplayName("List doctors when name filter provided should return matching doctors")
    void listDoctorsWhenNameFilterShouldReturnMatchingDoctors() {
        // given
        StaffMember doc1 = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "Jan", "Kowalski", "jan@test.com", "Cardiology", "1111111", null, null, null);
        StaffMember doc2 = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "Anna", "Nowak", "anna@test.com", "Neurology", "2222222", null, null, null);
        when(staffUseCase.getStaffByRole(StaffRole.DOCTOR)).thenReturn(List.of(doc1, doc2));

        // when
        ResponseEntity<List<StaffDto>> response = doctorController.listDoctors(null, "kowal");

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).lastName()).isEqualTo("Kowalski");
    }
}
