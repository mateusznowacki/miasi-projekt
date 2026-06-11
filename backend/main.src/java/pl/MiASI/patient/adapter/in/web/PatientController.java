package pl.MiASI.patient.adapter.in.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.MiASI.patient.application.port.in.PatientUseCase;
import pl.MiASI.shared.domain.model.PatientId;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientUseCase patientUseCase;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterReq req) {
        PatientId id = patientUseCase.registerPatient(req.firstName(), req.lastName(), req.pesel(), req.phone(), req.email(), req.password());
        return ResponseEntity.ok(Map.of("patientId", id.value().toString()));
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping("/{patientId}/medical-records")
    public ResponseEntity<Void> addMedicalRecord(@PathVariable UUID patientId, @Valid @RequestBody AddMedicalRecordReq req) {
        try {
            patientUseCase.addMedicalRecord(new PatientId(patientId), req.visitId(), new pl.MiASI.shared.domain.model.DoctorId(req.doctorId()), req.diagnoses(), req.symptoms(), req.prescriptions(), req.notes(), req.testResults());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/{patientId}/medical-records/{recordId}")
    public ResponseEntity<Void> updateMedicalRecord(@PathVariable UUID patientId, @PathVariable UUID recordId, @Valid @RequestBody UpdateMedicalRecordReq req, org.springframework.security.core.Authentication authentication) {
        try {
            pl.MiASI.shared.domain.model.DoctorId doctorId = new pl.MiASI.shared.domain.model.DoctorId(UUID.fromString(authentication.getName()));
            patientUseCase.updateMedicalRecord(new PatientId(patientId), recordId, req.diagnoses(), req.symptoms(), req.prescriptions(), req.notes(), req.testResults(), doctorId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN_STAFF', 'ADMIN') or authentication.name == #patientId.toString()")
    @GetMapping("/{patientId}/medical-records/visit/{visitId}")
    public ResponseEntity<pl.MiASI.patient.domain.model.MedicalRecord> getMedicalRecordByVisitId(@PathVariable UUID patientId, @PathVariable UUID visitId) {
        return patientUseCase.getMedicalRecordByVisitId(new PatientId(patientId), visitId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN_STAFF', 'ADMIN') or authentication.name == #patientId.toString()")
    @GetMapping("/{patientId}")
    public ResponseEntity<pl.MiASI.patient.domain.model.Patient> getPatientProfile(@PathVariable UUID patientId) {
        return patientUseCase.getPatientProfile(new PatientId(patientId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN_STAFF', 'ADMIN') or authentication.name == #patientId.toString()")
    @PutMapping("/{patientId}")
    public ResponseEntity<?> updatePersonalData(@PathVariable UUID patientId, @Valid @RequestBody UpdatePersonalDataReq req) {
        try {
            patientUseCase.updatePersonalData(new PatientId(patientId), req.firstName(), req.lastName(), req.phone(), req.email(), req.address());
            return ResponseEntity.ok(Map.of("message", "Dane zostały zaktualizowane."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN_STAFF', 'ADMIN') or authentication.name == #patientId.toString()")
    @GetMapping("/{patientId}/medical-records")
    public ResponseEntity<java.util.List<pl.MiASI.patient.domain.model.MedicalRecord>> getMedicalHistory(@PathVariable UUID patientId) {
        return patientUseCase.getPatientProfile(new PatientId(patientId))
                .map(p -> ResponseEntity.ok(p.getMedicalRecords()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<java.util.List<pl.MiASI.patient.domain.model.Patient>> searchPatients(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String pesel,
            @RequestParam(required = false) String patientCardNumber) {
        if ((firstName == null || firstName.isBlank()) &&
                (lastName == null || lastName.isBlank()) &&
                (pesel == null || pesel.isBlank()) &&
                (patientCardNumber == null || patientCardNumber.isBlank())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(patientUseCase.searchPatients(firstName, lastName, pesel, patientCardNumber));
    }
}

record RegisterReq(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String pesel,
        @NotBlank String phone,
        @NotBlank @Email String email,
        @NotBlank String password
) {
}

record UpdateMedicalRecordReq(
        @NotBlank String diagnoses,
        String symptoms,
        String prescriptions,
        String notes,
        String testResults
) {
}

record UpdatePersonalDataReq(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String phone,
        @NotBlank @Email String email,
        String address
) {
}

record AddMedicalRecordReq(
        UUID visitId,
        UUID doctorId,
        @NotBlank String diagnoses,
        String symptoms,
        String prescriptions,
        String notes,
        String testResults
) {
}