package pl.MiASI.medicalcare.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.MiASI.medicalcare.application.port.in.ScheduleQueryUseCase;
import pl.MiASI.medicalcare.application.port.in.VisitManagementUseCase;
import pl.MiASI.medicalcare.application.port.in.VisitQueryUseCase;
import pl.MiASI.medicalcare.domain.model.*;
import pl.MiASI.patient.application.port.in.PatientUseCase;
import pl.MiASI.shared.domain.model.DoctorId;
import pl.MiASI.shared.domain.model.PatientId;
import pl.MiASI.staff.application.port.in.StaffUseCase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitManagementUseCase visitManagementUseCase;
    private final VisitQueryUseCase visitQueryUseCase;
    private final StaffUseCase staffUseCase;
    private final PatientUseCase patientUseCase;
    private final ScheduleQueryUseCase scheduleQueryUseCase;

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
    public ResponseEntity<List<VisitDto>> getVisitsByPatient(
            @PathVariable UUID patientId,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) UUID doctorId,
            @RequestParam(required = false) String status) {
        List<Visit> visits = visitQueryUseCase.getVisitsByPatient(new PatientId(patientId));
        List<VisitDto> dtos = mapAndFilterVisits(visits, filter, startDate, endDate, doctorId, status);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<VisitDto>> getVisitsByDoctor(
            @PathVariable UUID doctorId,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) String status) {
        List<Visit> visits = visitQueryUseCase.getVisitsByDoctor(new DoctorId(doctorId));
        List<VisitDto> dtos = mapAndFilterVisits(visits, filter, startDate, endDate, patientId, status);
        return ResponseEntity.ok(dtos);
    }

    private List<VisitDto> mapAndFilterVisits(List<Visit> visits, String filter, LocalDateTime startDate, LocalDateTime endDate, UUID otherId, String status) {
        return visits.stream()
                .map(this::enrichVisit)
                .filter(dto -> {
                    LocalDateTime dtoDate = dto.date() != null && !dto.date().isEmpty() ? LocalDateTime.parse(dto.date()) : null;
                    if ("FUTURE".equalsIgnoreCase(filter) || "upcoming".equalsIgnoreCase(filter)) {
                        if (!"Zarezerwowana".equalsIgnoreCase(dto.status())) return false;
                    }
                    if ("PAST".equalsIgnoreCase(filter) || "past".equalsIgnoreCase(filter)) {
                        if ("Zarezerwowana".equalsIgnoreCase(dto.status())) return false;
                    }
                    if (startDate != null && dtoDate != null && dtoDate.isBefore(startDate)) return false;
                    if (endDate != null && dtoDate != null && dtoDate.isAfter(endDate)) return false;
                    if (otherId != null && !dto.doctorId().equals(otherId) && !dto.patientId().equals(otherId))
                        return false;
                    return status == null || dto.status().equalsIgnoreCase(status);
                })
                .collect(Collectors.toList());
    }

    private VisitDto enrichVisit(Visit visit) {
        String patientName = patientUseCase.getPatientProfile(visit.getPatientId())
                .map(p -> p.getFirstName() + " " + p.getLastName())
                .orElse("Nieznany Pacjent");

        String doctorName = staffUseCase.getStaffById(visit.getDoctorId().value())
                .map(s -> s.getFirstName() + " " + s.getLastName())
                .orElse("Nieznany Lekarz");

        Schedule schedule = scheduleQueryUseCase.getScheduleByDoctor(visit.getDoctorId()).orElse(null);
        LocalDateTime visitDate = null;
        String room = "Brak gabinetu";

        if (schedule != null && !visit.getSlotIds().isEmpty()) {
            SlotId firstSlotId = visit.getSlotIds().get(0);
            Optional<Slot> slotOpt = schedule.slots().stream()
                    .filter(s -> s.getSlotId().equals(firstSlotId))
                    .findFirst();
            if (slotOpt.isPresent()) {
                visitDate = slotOpt.get().getTimeRange().startTime();
                room = slotOpt.get().getOffice();
            }
        }

        return new VisitDto(
                visit.getVisitId().value(),
                visitDate != null ? visitDate.toString() : "",
                visit.getDoctorId().value(),
                doctorName,
                visit.getPatientId().value(),
                patientName,
                mapStatusToFrontend(visit.getStatus().name()),
                visit.getConsultationType().name(),
                room
        );
    }

    private String mapStatusToFrontend(String backendStatus) {
        if ("RESERVED".equals(backendStatus)) return "Zarezerwowana";
        if ("COMPLETED".equals(backendStatus)) return "Zakończona";
        if ("CANCELED".equals(backendStatus)) return "Anulowana";
        return backendStatus;
    }
}

record ReserveVisitRequest(UUID patientId, UUID doctorId, ConsultationType type, List<UUID> slotIds) {
}

record VisitDto(UUID id, String date, UUID doctorId, String doctorName, UUID patientId, String patientName,
                String status, String type, String room) {
}
