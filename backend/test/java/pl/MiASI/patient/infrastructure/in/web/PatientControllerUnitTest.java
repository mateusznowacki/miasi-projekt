package pl.MiASI.patient.infrastructure.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import pl.MiASI.patient.application.domain.model.MedicalRecord;
import pl.MiASI.patient.application.domain.model.Patient;
import pl.MiASI.patient.application.port.in.PatientUseCase;
import pl.MiASI.shared.application.domain.model.DoctorId;
import pl.MiASI.shared.application.domain.model.PatientId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerUnitTest {

    @Mock
    private PatientUseCase patientUseCase;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PatientController patientController;

    private final UUID patientUuid = UUID.randomUUID();
    private final PatientId patientId = new PatientId(patientUuid);
    private final UUID recordUuid = UUID.randomUUID();
    private final UUID visitUuid = UUID.randomUUID();
    private final UUID doctorUuid = UUID.randomUUID();

    @Test
    @DisplayName("Should successfully register and return patient id")
    void registerWhenValidDataShouldReturnOk() {
        // given
        RegisterReq req = new RegisterReq("A", "B", "123", "111", "a@b.com", "pass");
        when(patientUseCase.registerPatient(req.firstName(), req.lastName(), req.pesel(), req.phone(), req.email(), req.password()))
                .thenReturn(patientId);

        // when
        ResponseEntity<Map<String, String>> response = patientController.register(req);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(patientUuid.toString(), response.getBody().get("patientId"));
    }

    @Test
    @DisplayName("Should add medical record successfully")
    void addMedicalRecordWhenValidDataShouldReturnOk() {
        // given
        AddMedicalRecordReq req = new AddMedicalRecordReq(visitUuid, doctorUuid, "diag", "symp", "presc", "notes", "test");

        // when
        ResponseEntity<Void> response = patientController.addMedicalRecord(patientUuid, req);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(patientUseCase).addMedicalRecord(any(), eq(visitUuid), any(), eq("diag"), eq("symp"), eq("presc"), eq("notes"), eq("test"));
    }

    @Test
    @DisplayName("Should return bad request when adding medical record throws exception")
    void addMedicalRecordWhenIllegalArgumentExceptionShouldReturnBadRequest() {
        // given
        AddMedicalRecordReq req = new AddMedicalRecordReq(visitUuid, doctorUuid, "", "symp", "presc", "notes", "test");
        doThrow(new IllegalArgumentException("Error")).when(patientUseCase).addMedicalRecord(any(), any(), any(), any(), any(), any(), any(), any());

        // when
        ResponseEntity<Void> response = patientController.addMedicalRecord(patientUuid, req);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should update medical record successfully")
    void updateMedicalRecordWhenValidDataShouldReturnOk() {
        // given
        UpdateMedicalRecordReq req = new UpdateMedicalRecordReq("diag", "symp", "presc", "notes", "test");
        when(authentication.getName()).thenReturn(doctorUuid.toString());

        // when
        ResponseEntity<Void> response = patientController.updateMedicalRecord(patientUuid, recordUuid, req, authentication);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(patientUseCase).updateMedicalRecord(any(), eq(recordUuid), eq("diag"), eq("symp"), eq("presc"), eq("notes"), eq("test"), any());
    }

    @Test
    @DisplayName("Should return bad request when updating medical record throws exception")
    void updateMedicalRecordWhenIllegalArgumentExceptionShouldReturnBadRequest() {
        // given
        UpdateMedicalRecordReq req = new UpdateMedicalRecordReq("", "symp", "presc", "notes", "test");
        when(authentication.getName()).thenReturn(doctorUuid.toString());
        doThrow(new IllegalArgumentException("Error")).when(patientUseCase).updateMedicalRecord(any(), any(), any(), any(), any(), any(), any(), any());

        // when
        ResponseEntity<Void> response = patientController.updateMedicalRecord(patientUuid, recordUuid, req, authentication);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return medical record by visit ID when found")
    void getMedicalRecordByVisitIdWhenFoundShouldReturnOk() {
        // given
        MedicalRecord record = new MedicalRecord(recordUuid, visitUuid, new DoctorId(doctorUuid), "diag", "symp", "presc", "notes", "test", LocalDateTime.now(), null, null);
        when(patientUseCase.getMedicalRecordByVisitId(any(), eq(visitUuid))).thenReturn(Optional.of(record));

        // when
        ResponseEntity<MedicalRecord> response = patientController.getMedicalRecordByVisitId(patientUuid, visitUuid);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(record, response.getBody());
    }

    @Test
    @DisplayName("Should return not found when medical record by visit ID does not exist")
    void getMedicalRecordByVisitIdWhenNotFoundShouldReturnNotFound() {
        // given
        when(patientUseCase.getMedicalRecordByVisitId(any(), eq(visitUuid))).thenReturn(Optional.empty());

        // when
        ResponseEntity<MedicalRecord> response = patientController.getMedicalRecordByVisitId(patientUuid, visitUuid);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return patient profile when found")
    void getPatientProfileWhenFoundShouldReturnOk() {
        // given
        Patient patient = Patient.create(patientId, "A", "B", "123", "1", "a");
        when(patientUseCase.getPatientProfile(any())).thenReturn(Optional.of(patient));

        // when
        ResponseEntity<Patient> response = patientController.getPatientProfile(patientUuid);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(patient, response.getBody());
    }

    @Test
    @DisplayName("Should return not found when patient profile does not exist")
    void getPatientProfileWhenNotFoundShouldReturnNotFound() {
        // given
        when(patientUseCase.getPatientProfile(any())).thenReturn(Optional.empty());

        // when
        ResponseEntity<Patient> response = patientController.getPatientProfile(patientUuid);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should update personal data successfully")
    void updatePersonalDataWhenValidDataShouldReturnOk() {
        // given
        UpdatePersonalDataReq req = new UpdatePersonalDataReq("A", "B", "1", "a@b.com", "Addr");

        // when
        ResponseEntity<?> response = patientController.updatePersonalData(patientUuid, req);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(patientUseCase).updatePersonalData(any(), eq("A"), eq("B"), eq("1"), eq("a@b.com"), eq("Addr"));
    }

    @Test
    @DisplayName("Should return bad request when update personal data throws exception")
    void updatePersonalDataWhenExceptionShouldReturnBadRequest() {
        // given
        UpdatePersonalDataReq req = new UpdatePersonalDataReq("A", "B", "1", "a@b.com", "Addr");
        doThrow(new IllegalArgumentException("Error")).when(patientUseCase).updatePersonalData(any(), any(), any(), any(), any(), any());

        // when
        ResponseEntity<?> response = patientController.updatePersonalData(patientUuid, req);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return medical history when patient found")
    void getMedicalHistoryWhenPatientFoundShouldReturnOk() {
        // given
        Patient patient = Patient.create(patientId, "A", "B", "123", "1", "a");
        MedicalRecord record = new MedicalRecord(recordUuid, visitUuid, new DoctorId(doctorUuid), "diag", "symp", "presc", "notes", "test", LocalDateTime.now(), null, null);
        patient.addMedicalRecord(record);
        when(patientUseCase.getPatientProfile(any())).thenReturn(Optional.of(patient));

        // when
        ResponseEntity<List<MedicalRecord>> response = patientController.getMedicalHistory(patientUuid);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("Should return not found for medical history when patient not found")
    void getMedicalHistoryWhenPatientNotFoundShouldReturnNotFound() {
        // given
        when(patientUseCase.getPatientProfile(any())).thenReturn(Optional.empty());

        // when
        ResponseEntity<List<MedicalRecord>> response = patientController.getMedicalHistory(patientUuid);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return bad request for search when all params are blank")
    void searchPatientsWhenAllParamsBlankShouldReturnBadRequest() {
        // when
        ResponseEntity<List<Patient>> response = patientController.searchPatients(null, "", " ", null);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should search patients when any param is valid")
    void searchPatientsWhenValidParamsShouldReturnOk() {
        // given
        Patient patient = Patient.create(patientId, "A", "B", "123", "1", "a");
        when(patientUseCase.searchPatients("A", null, null, null)).thenReturn(List.of(patient));

        // when
        ResponseEntity<List<Patient>> response = patientController.searchPatients("A", null, null, null);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}
