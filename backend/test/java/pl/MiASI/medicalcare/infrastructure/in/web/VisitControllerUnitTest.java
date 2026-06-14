package pl.MiASI.medicalcare.infrastructure.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.MiASI.medicalcare.application.domain.model.*;
import pl.MiASI.medicalcare.application.port.in.AddSlotCommand;
import pl.MiASI.medicalcare.application.port.in.ScheduleQueryUseCase;
import pl.MiASI.medicalcare.application.port.in.VisitManagementUseCase;
import pl.MiASI.medicalcare.application.port.in.VisitQueryUseCase;
import pl.MiASI.patient.application.domain.model.Patient;
import pl.MiASI.patient.application.port.in.PatientUseCase;
import pl.MiASI.shared.application.domain.model.DoctorId;
import pl.MiASI.shared.application.domain.model.PatientId;
import pl.MiASI.staff.application.domain.model.StaffMember;
import pl.MiASI.staff.application.domain.model.StaffRole;
import pl.MiASI.staff.application.port.in.StaffUseCase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitControllerUnitTest {

    @Mock
    private VisitManagementUseCase visitManagementUseCase;

    @Mock
    private VisitQueryUseCase visitQueryUseCase;

    @Mock
    private StaffUseCase staffUseCase;

    @Mock
    private PatientUseCase patientUseCase;

    @Mock
    private ScheduleQueryUseCase scheduleQueryUseCase;

    @InjectMocks
    private VisitController controller;

    @Test
    @DisplayName("Should reserve visit and return 200 OK with Visit UUID")
    void reserveVisitWhenValidRequestShouldReturnOk() {
        // given
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();
        ReserveVisitRequest request = new ReserveVisitRequest(patientId, doctorId, ConsultationType.GENERAL, List.of(slotId));
        VisitId visitId = new VisitId();
        
        when(visitManagementUseCase.reserveVisit(eq(new PatientId(patientId)), eq(new DoctorId(doctorId)), eq(ConsultationType.GENERAL), any())).thenReturn(visitId);

        // when
        ResponseEntity<UUID> response = controller.reserveVisit(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(visitId.value());
    }

    @Test
    @DisplayName("Should cancel visit and return 200 OK")
    void cancelVisitWhenValidRequestShouldReturnOk() {
        // given
        UUID visitIdUuid = UUID.randomUUID();

        // when
        ResponseEntity<Void> response = controller.cancelVisit(visitIdUuid);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(visitManagementUseCase).cancelVisit(eq(new VisitId(visitIdUuid)));
    }

    @Test
    @DisplayName("Should get visits by patient and enrich them")
    void getVisitsByPatientShouldReturnEnrichedVisits() {
        // given
        UUID patientUuid = UUID.randomUUID();
        UUID doctorUuid = UUID.randomUUID();
        UUID slotUuid = UUID.randomUUID();

        Visit visit = Visit.reserve(new PatientId(patientUuid), new DoctorId(doctorUuid), ConsultationType.GENERAL, List.of(new SlotId(slotUuid)));
        when(visitQueryUseCase.getVisitsByPatient(new PatientId(patientUuid))).thenReturn(List.of(visit));

        Patient patientProfile = mock(Patient.class);
        when(patientProfile.getFirstName()).thenReturn("Jane");
        when(patientProfile.getLastName()).thenReturn("Doe");
        when(patientUseCase.getPatientProfile(new PatientId(patientUuid))).thenReturn(Optional.of(patientProfile));

        StaffMember staffMember = mock(StaffMember.class);
        when(staffMember.getFirstName()).thenReturn("John");
        when(staffMember.getLastName()).thenReturn("Smith");
        when(staffUseCase.getStaffById(doctorUuid)).thenReturn(Optional.of(staffMember));

        Schedule schedule = Schedule.create(new DoctorId(doctorUuid));
        // Manually add slot with the exact UUID to match
        Slot slot = new Slot(new SlotId(slotUuid), new TimeRange(LocalDateTime.of(2023, 10, 10, 10, 0), LocalDateTime.of(2023, 10, 10, 11, 0)), "Room 101", SlotStatus.BOOKED);
        schedule.slots().add(slot);
        when(scheduleQueryUseCase.getScheduleByDoctor(new DoctorId(doctorUuid))).thenReturn(Optional.of(schedule));

        // when
        ResponseEntity<List<VisitDto>> response = controller.getVisitsByPatient(patientUuid, null, null, null, null, null);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<VisitDto> dtos = response.getBody();
        assertThat(dtos).hasSize(1);
        VisitDto dto = dtos.get(0);
        assertThat(dto.patientName()).isEqualTo("Jane Doe");
        assertThat(dto.doctorName()).isEqualTo("John Smith");
        assertThat(dto.room()).isEqualTo("Room 101");
        assertThat(dto.status()).isEqualTo("Zarezerwowana");
    }

    @Test
    @DisplayName("Should filter visits by future parameter")
    void getVisitsByPatientWhenFilterFutureShouldOnlyReturnReserved() {
        // given
        UUID patientUuid = UUID.randomUUID();
        UUID doctorUuid = UUID.randomUUID();

        Visit visit1 = Visit.reserve(new PatientId(patientUuid), new DoctorId(doctorUuid), ConsultationType.GENERAL, List.of(new SlotId()));
        Visit visit2 = Visit.reserve(new PatientId(patientUuid), new DoctorId(doctorUuid), ConsultationType.GENERAL, List.of(new SlotId()));
        visit2.cancel(); // Canceled visit
        
        when(visitQueryUseCase.getVisitsByPatient(new PatientId(patientUuid))).thenReturn(List.of(visit1, visit2));
        when(patientUseCase.getPatientProfile(any())).thenReturn(Optional.empty());
        when(staffUseCase.getStaffById(any())).thenReturn(Optional.empty());

        // when
        ResponseEntity<List<VisitDto>> response = controller.getVisitsByPatient(patientUuid, "future", null, null, null, null);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<VisitDto> dtos = response.getBody();
        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).status()).isEqualTo("Zarezerwowana");
    }

    @Test
    @DisplayName("Should get visits by doctor and enrich them")
    void getVisitsByDoctorShouldReturnEnrichedVisits() {
        // given
        UUID doctorUuid = UUID.randomUUID();
        Visit visit = Visit.reserve(new PatientId(UUID.randomUUID()), new DoctorId(doctorUuid), ConsultationType.GENERAL, List.of(new SlotId()));
        when(visitQueryUseCase.getVisitsByDoctor(new DoctorId(doctorUuid))).thenReturn(List.of(visit));
        
        when(patientUseCase.getPatientProfile(any())).thenReturn(Optional.empty());
        when(staffUseCase.getStaffById(any())).thenReturn(Optional.empty());

        // when
        ResponseEntity<List<VisitDto>> response = controller.getVisitsByDoctor(doctorUuid, null, null, null, null, null);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).doctorName()).isEqualTo("Nieznany Lekarz");
    }
}
