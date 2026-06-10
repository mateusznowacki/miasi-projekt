package pl.MiASI.medicalcare.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.MiASI.shared.domain.model.DoctorId;
import pl.MiASI.shared.domain.model.PatientId;
import pl.MiASI.medicalcare.application.port.in.VisitManagementUseCase;
import pl.MiASI.medicalcare.application.port.in.VisitQueryUseCase;
import pl.MiASI.medicalcare.domain.model.ConsultationType;
import pl.MiASI.medicalcare.domain.model.SlotId;
import pl.MiASI.medicalcare.domain.model.Visit;
import pl.MiASI.medicalcare.domain.model.VisitId;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitManagementUseCase visitManagementUseCase;
    private final VisitQueryUseCase visitQueryUseCase;

    @PostMapping
    public ResponseEntity<UUID> reserveVisit(@RequestBody ReserveVisitRequest request) {
        VisitId visitId = visitManagementUseCase.reserveVisit(
                new PatientId(request.patientId()),
                new DoctorId(request.doctorId()),
                request.type(),
                request.slotIds().stream().map(SlotId::new).collect(Collectors.toList())
        );
        return ResponseEntity.ok(visitId.value());
    }

    @PostMapping("/{visitId}/cancel")
    public ResponseEntity<Void> cancelVisit(@PathVariable UUID visitId) {
        visitManagementUseCase.cancelVisit(new VisitId(visitId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<VisitDto>> getVisitsByPatient(@PathVariable UUID patientId, @RequestParam(required = false) String filter) {
        List<VisitDto> visits = filterVisits(visitQueryUseCase.getVisitsByPatient(new PatientId(patientId)), filter);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<VisitDto>> getVisitsByDoctor(@PathVariable UUID doctorId, @RequestParam(required = false) String filter) {
        List<VisitDto> visits = filterVisits(visitQueryUseCase.getVisitsByDoctor(new DoctorId(doctorId)), filter);
        return ResponseEntity.ok(visits);
    }

    private List<VisitDto> filterVisits(List<Visit> visits, String filter) {
        return visits.stream()
                .filter(v -> {
                    if ("FUTURE".equalsIgnoreCase(filter)) return v.getStatus() == pl.MiASI.medicalcare.domain.model.VisitStatus.RESERVED;
                    if ("PAST".equalsIgnoreCase(filter)) return v.getStatus() != pl.MiASI.medicalcare.domain.model.VisitStatus.RESERVED;
                    return true;
                })
                .map(VisitDto::fromDomain)
                .collect(Collectors.toList());
    }
}

record ReserveVisitRequest(UUID patientId, UUID doctorId, ConsultationType type, List<UUID> slotIds) {}

record VisitDto(UUID visitId, UUID patientId, UUID doctorId, String type, String status, List<UUID> slotIds) {
    static VisitDto fromDomain(Visit visit) {
        return new VisitDto(
                visit.getVisitId().value(),
                visit.getPatientId().value(),
                visit.getDoctorId().value(),
                visit.getConsultationType().name(),
                visit.getStatus().name(),
                visit.getSlotIds().stream().map(SlotId::value).collect(Collectors.toList())
        );
    }
}
