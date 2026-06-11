package pl.MiASI.patient.adapter.in.web;

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
import pl.MiASI.patient.application.port.in.PatientUseCase;
import pl.MiASI.shared.domain.model.PatientId;
import pl.MiASI.shared.domain.model.DoctorId;
import pl.MiASI.patient.domain.model.Patient;
import pl.MiASI.patient.domain.model.MedicalRecord;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
class PatientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PatientUseCase patientUseCase;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void shouldRegisterPatient() throws Exception {
        RegisterReq req = new RegisterReq("John", "Doe", "12345678901", "123456789", "john@example.com", "password");
        PatientId expectedId = new PatientId(UUID.randomUUID());

        when(patientUseCase.registerPatient(req.firstName(), req.lastName(), req.pesel(), req.phone(), req.email(), req.password()))
                .thenReturn(expectedId);

        mockMvc.perform(post("/api/patients/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(expectedId.value().toString()));
    }

    @Test
    void shouldReturnBadRequestWhenRegisterWithInvalidData() throws Exception {
        RegisterReq req = new RegisterReq("", "Doe", "123", "", "invalid-email", "password");

        mockMvc.perform(post("/api/patients/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "DOCTOR", username = "123e4567-e89b-12d3-a456-426614174000")
    void shouldAddMedicalRecord() throws Exception {
        UUID patientId = UUID.randomUUID();
        AddMedicalRecordReq req = new AddMedicalRecordReq(UUID.randomUUID(), UUID.randomUUID(), "Diagnoses", "Symptoms", "Prescriptions", "Notes", "Test Results");

        mockMvc.perform(post("/api/patients/{patientId}/medical-records", patientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(patientUseCase, times(1)).addMedicalRecord(
                eq(new PatientId(patientId)),
                eq(req.visitId()),
                eq(new DoctorId(req.doctorId())),
                eq(req.diagnoses()),
                eq(req.symptoms()),
                eq(req.prescriptions()),
                eq(req.notes()),
                eq(req.testResults())
        );
    }

    @Test
    @WithMockUser(roles = "DOCTOR", username = "123e4567-e89b-12d3-a456-426614174000")
    void shouldReturnBadRequestWhenAddMedicalRecordThrowsException() throws Exception {
        UUID patientId = UUID.randomUUID();
        AddMedicalRecordReq req = new AddMedicalRecordReq(UUID.randomUUID(), UUID.randomUUID(), "Diagnoses", null, null, null, null);

        doThrow(new IllegalArgumentException("Error")).when(patientUseCase).addMedicalRecord(any(), any(), any(), any(), any(), any(), any(), any());

        mockMvc.perform(post("/api/patients/{patientId}/medical-records", patientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "DOCTOR", username = "123e4567-e89b-12d3-a456-426614174000")
    void shouldUpdateMedicalRecord() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID recordId = UUID.randomUUID();
        UpdateMedicalRecordReq req = new UpdateMedicalRecordReq("New Diagnoses", "New Symptoms", "New Prescriptions", "New Notes", "New Test Results");

        mockMvc.perform(put("/api/patients/{patientId}/medical-records/{recordId}", patientId, recordId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("123e4567-e89b-12d3-a456-426614174000", "password")))
                .andExpect(status().isOk());

        verify(patientUseCase, times(1)).updateMedicalRecord(
                eq(new PatientId(patientId)),
                eq(recordId),
                eq(req.diagnoses()),
                eq(req.symptoms()),
                eq(req.prescriptions()),
                eq(req.notes()),
                eq(req.testResults()),
                eq(new DoctorId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")))
        );
    }

    @Test
    @WithMockUser(roles = "DOCTOR", username = "123e4567-e89b-12d3-a456-426614174000")
    void shouldReturnBadRequestWhenUpdateMedicalRecordThrowsException() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID recordId = UUID.randomUUID();
        UpdateMedicalRecordReq req = new UpdateMedicalRecordReq("New Diagnoses", null, null, null, null);

        doThrow(new IllegalArgumentException("Error")).when(patientUseCase).updateMedicalRecord(any(), any(), any(), any(), any(), any(), any(), any());

        mockMvc.perform(put("/api/patients/{patientId}/medical-records/{recordId}", patientId, recordId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .principal(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("123e4567-e89b-12d3-a456-426614174000", "password")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "DOCTOR", username = "123e4567-e89b-12d3-a456-426614174000")
    void shouldGetMedicalRecordByVisitId() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID visitId = UUID.randomUUID();
        MedicalRecord mockRecord = mock(MedicalRecord.class);

        when(patientUseCase.getMedicalRecordByVisitId(eq(new PatientId(patientId)), eq(visitId)))
                .thenReturn(Optional.of(mockRecord));

        mockMvc.perform(get("/api/patients/{patientId}/medical-records/visit/{visitId}", patientId, visitId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DOCTOR", username = "123e4567-e89b-12d3-a456-426614174000")
    void shouldReturnNotFoundWhenMedicalRecordByVisitIdMissing() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID visitId = UUID.randomUUID();

        when(patientUseCase.getMedicalRecordByVisitId(eq(new PatientId(patientId)), eq(visitId)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/patients/{patientId}/medical-records/visit/{visitId}", patientId, visitId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "PATIENT", username = "123e4567-e89b-12d3-a456-426614174000")
    void shouldGetPatientProfile() throws Exception {
        UUID patientId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        Patient mockPatient = mock(Patient.class);

        when(patientUseCase.getPatientProfile(eq(new PatientId(patientId))))
                .thenReturn(Optional.of(mockPatient));

        mockMvc.perform(get("/api/patients/{patientId}", patientId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PATIENT", username = "123e4567-e89b-12d3-a456-426614174000")
    void shouldReturnNotFoundWhenGetPatientProfileMissing() throws Exception {
        UUID patientId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        when(patientUseCase.getPatientProfile(eq(new PatientId(patientId))))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/patients/{patientId}", patientId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "PATIENT", username = "123e4567-e89b-12d3-a456-426614174000")
    void shouldUpdatePersonalData() throws Exception {
        UUID patientId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UpdatePersonalDataReq req = new UpdatePersonalDataReq("John", "Doe", "123456789", "john@example.com", "Address");

        mockMvc.perform(put("/api/patients/{patientId}", patientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Dane zostały zaktualizowane."));

        verify(patientUseCase, times(1)).updatePersonalData(
                eq(new PatientId(patientId)),
                eq(req.firstName()),
                eq(req.lastName()),
                eq(req.phone()),
                eq(req.email()),
                eq(req.address())
        );
    }

    @Test
    @WithMockUser(roles = "PATIENT", username = "123e4567-e89b-12d3-a456-426614174000")
    void shouldReturnBadRequestWhenUpdatePersonalDataThrowsException() throws Exception {
        UUID patientId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UpdatePersonalDataReq req = new UpdatePersonalDataReq("John", "Doe", "123456789", "john@example.com", "Address");

        doThrow(new IllegalArgumentException("Error")).when(patientUseCase).updatePersonalData(any(), any(), any(), any(), any(), any());

        mockMvc.perform(put("/api/patients/{patientId}", patientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error"));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldSearchPatients() throws Exception {
        Patient mockPatient = mock(Patient.class);

        when(patientUseCase.searchPatients("John", null, null, null))
                .thenReturn(List.of(mockPatient));

        mockMvc.perform(get("/api/patients/search")
                .param("firstName", "John"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldReturnBadRequestWhenSearchPatientsWithoutParams() throws Exception {
        mockMvc.perform(get("/api/patients/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "PATIENT", username = "123e4567-e89b-12d3-a456-426614174000")
    void shouldGetMedicalHistory() throws Exception {
        UUID patientId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        Patient mockPatient = mock(Patient.class);
        MedicalRecord mockRecord = mock(MedicalRecord.class);

        when(mockPatient.getMedicalRecords()).thenReturn(List.of(mockRecord));
        when(patientUseCase.getPatientProfile(eq(new PatientId(patientId))))
                .thenReturn(Optional.of(mockPatient));

        mockMvc.perform(get("/api/patients/{patientId}/medical-records", patientId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PATIENT", username = "123e4567-e89b-12d3-a456-426614174000")
    void shouldReturnNotFoundWhenGetMedicalHistoryMissingPatient() throws Exception {
        UUID patientId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        when(patientUseCase.getPatientProfile(eq(new PatientId(patientId))))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/patients/{patientId}/medical-records", patientId))
                .andExpect(status().isNotFound());
    }
}
