package pl.MiASI.medicalcare.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pl.MiASI.iam.adapter.out.security.JwtTokenProvider;
import pl.MiASI.medicalcare.application.port.in.ScheduleQueryUseCase;
import pl.MiASI.medicalcare.application.port.in.VisitManagementUseCase;
import pl.MiASI.medicalcare.application.port.in.VisitQueryUseCase;
import pl.MiASI.medicalcare.domain.model.ConsultationType;
import pl.MiASI.medicalcare.domain.model.Visit;
import pl.MiASI.medicalcare.domain.model.VisitId;
import pl.MiASI.medicalcare.domain.model.VisitStatus;
import pl.MiASI.patient.application.port.in.PatientUseCase;
import pl.MiASI.patient.domain.model.Patient;
import pl.MiASI.shared.domain.model.DoctorId;
import pl.MiASI.shared.domain.model.PatientId;
import pl.MiASI.staff.application.port.in.StaffUseCase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = VisitController.class)
@AutoConfigureMockMvc(addFilters = false)
class VisitControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @MockitoBean
    private VisitManagementUseCase visitManagementUseCase;

    @MockitoBean
    private VisitQueryUseCase visitQueryUseCase;

    @MockitoBean
    private StaffUseCase staffUseCase;

    @MockitoBean
    private PatientUseCase patientUseCase;

    @MockitoBean
    private ScheduleQueryUseCase scheduleQueryUseCase;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(roles = "PATIENT")
    void shouldReserveVisit() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();
        UUID expectedVisitId = UUID.randomUUID();

        ReserveVisitRequest request = new ReserveVisitRequest(patientId, doctorId, ConsultationType.GENERAL, List.of(slotId));

        when(visitManagementUseCase.reserveVisit(any(), any(), any(), any())).thenReturn(new VisitId(expectedVisitId));

        mockMvc.perform(post("/api/visits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedVisitId.toString()));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void shouldReturnBadRequestWhenReserveVisitThrowsException() throws Exception {
        UUID patientId = UUID.randomUUID();
        ReserveVisitRequest request = new ReserveVisitRequest(patientId, UUID.randomUUID(), ConsultationType.GENERAL, List.of(UUID.randomUUID()));

        doThrow(new IllegalArgumentException("Slot taken")).when(visitManagementUseCase).reserveVisit(any(), any(), any(), any());

        mockMvc.perform(post("/api/visits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Slot taken"));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void shouldReturnConflictWhenReserveVisitThrowsIllegalState() throws Exception {
        UUID patientId = UUID.randomUUID();
        ReserveVisitRequest request = new ReserveVisitRequest(patientId, UUID.randomUUID(), ConsultationType.GENERAL, List.of(UUID.randomUUID()));

        doThrow(new IllegalStateException("Conflict")).when(visitManagementUseCase).reserveVisit(any(), any(), any(), any());

        mockMvc.perform(post("/api/visits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Conflict"));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void shouldCancelVisit() throws Exception {
        UUID visitId = UUID.randomUUID();

        mockMvc.perform(post("/api/visits/{visitId}/cancel", visitId))
                .andExpect(status().isOk());

        verify(visitManagementUseCase, times(1)).cancelVisit(eq(new VisitId(visitId)));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void shouldReturnBadRequestWhenCancelVisitThrowsException() throws Exception {
        UUID visitId = UUID.randomUUID();

        doThrow(new IllegalArgumentException("Cannot cancel")).when(visitManagementUseCase).cancelVisit(any());

        mockMvc.perform(post("/api/visits/{visitId}/cancel", visitId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void shouldGetVisitsByPatient() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        Visit visit = new Visit(new VisitId(UUID.randomUUID()), new PatientId(patientId), new DoctorId(doctorId), ConsultationType.GENERAL, VisitStatus.RESERVED, List.of());

        when(visitQueryUseCase.getVisitsByPatient(new PatientId(patientId))).thenReturn(List.of(visit));

        Patient mockPatient = mock(Patient.class);
        when(mockPatient.getFirstName()).thenReturn("John");
        when(mockPatient.getLastName()).thenReturn("Doe");
        when(patientUseCase.getPatientProfile(new PatientId(patientId))).thenReturn(Optional.of(mockPatient));

        mockMvc.perform(get("/api/visits/patient/{patientId}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].patientName").value("John Doe"));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldGetVisitsByDoctor() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        Visit visit = new Visit(new VisitId(UUID.randomUUID()), new PatientId(patientId), new DoctorId(doctorId), ConsultationType.GENERAL, VisitStatus.RESERVED, List.of());

        when(visitQueryUseCase.getVisitsByDoctor(new DoctorId(doctorId))).thenReturn(List.of(visit));

        mockMvc.perform(get("/api/visits/doctor/{doctorId}", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
