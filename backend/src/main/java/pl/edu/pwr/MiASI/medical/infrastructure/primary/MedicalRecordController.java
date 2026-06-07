package pl.edu.pwr.MiASI.medical.infrastructure.primary;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwr.MiASI.medical.application.FillMedicalRecordUseCase;
import pl.edu.pwr.MiASI.medical.domain.*;
import pl.edu.pwr.MiASI.staff.domain.DoctorId;

import java.util.UUID;

@RestController
@RequestMapping("/api/rekordy")
public class MedicalRecordController {
    private final FillMedicalRecordUseCase wypelnienieRekorduUseCase;

    public MedicalRecordController(FillMedicalRecordUseCase wypelnienieRekorduUseCase) {
        this.wypelnienieRekorduUseCase = wypelnienieRekorduUseCase;
    }

    @PostMapping("/fill")
    public ResponseEntity<Void> fillRecord(@RequestBody WypelnijRekordRequest request) {
        wypelnienieRekorduUseCase.execute(
            new AppointmentId(request.wizytaId()),
            new Diagnosis(request.kodICD10(), request.diagnosisDescription()),
            new Symptom(request.symptomDescription()),
            new Prescription(request.prescriptionMedication(), request.prescriptionDosage()),
            request.notes(),
            new PatientId(request.patientId()),
            new DoctorId(request.lekarzId())
        );
        return ResponseEntity.ok().build();
    }
}

record WypelnijRekordRequest(UUID wizytaId, UUID patientId, UUID lekarzId, String kodICD10, String diagnosisDescription, String symptomDescription, String prescriptionMedication, String prescriptionDosage, String notes) {}
