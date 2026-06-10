package pl.MiASI.patient.adapter.in.web;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterReq req) {
        PatientId id = patientUseCase.registerPatient(req.firstName(), req.lastName(), req.pesel(), req.phone(), req.email(), req.password());
        return ResponseEntity.ok(Map.of("patientId", id.value().toString()));
    }

    @PutMapping("/{patientId}/medical-records/{recordId}")
    public ResponseEntity<Void> updateMedicalRecord(@PathVariable UUID patientId, @PathVariable UUID recordId, @RequestBody UpdateMedicalRecordReq req) {
        patientUseCase.updateMedicalRecord(new PatientId(patientId), recordId, req.diagnoses(), req.symptoms(), req.prescriptions(), req.notes());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{patientId}/medical-records/visit/{visitId}")
    public ResponseEntity<pl.MiASI.patient.domain.model.MedicalRecord> getMedicalRecordByVisitId(@PathVariable UUID patientId, @PathVariable UUID visitId) {
        return patientUseCase.getMedicalRecordByVisitId(new PatientId(patientId), visitId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<java.util.List<pl.MiASI.patient.domain.model.Patient>> searchPatients(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(patientUseCase.searchPatients(query));
    }
}
record RegisterReq(String firstName, String lastName, String pesel, String phone, String email, String password) {}
record UpdateMedicalRecordReq(String diagnoses, String symptoms, String prescriptions, String notes) {}