package pl.MiASI.medicalcare.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.MiASI.medicalcare.application.port.in.ScheduleQueryUseCase;
import pl.MiASI.medicalcare.domain.model.Schedule;
import pl.MiASI.medicalcare.domain.model.Slot;
import pl.MiASI.staff.application.port.in.StaffUseCase;
import pl.MiASI.staff.domain.model.StaffMember;
import pl.MiASI.staff.domain.model.StaffRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules/available")
@RequiredArgsConstructor
public class AvailableSlotController {

    private final StaffUseCase staffUseCase;
    private final ScheduleQueryUseCase scheduleQueryUseCase;

    @GetMapping
    public ResponseEntity<List<AvailableSlotDto>> getAvailableSlots(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String doctorLastName,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        List<StaffMember> doctors = staffUseCase.getStaffByRole(StaffRole.DOCTOR).stream()
                .filter(d -> specialization == null || (d.getSpecialization() != null && d.getSpecialization().equalsIgnoreCase(specialization)))
                .filter(d -> doctorLastName == null || d.getLastName().toLowerCase().contains(doctorLastName.toLowerCase()))
                .collect(Collectors.toList());

        List<UUID> matchingDoctorIds = doctors.stream().map(StaffMember::getId).collect(Collectors.toList());

        List<Schedule> schedules = scheduleQueryUseCase.getAllSchedules().stream()
                .filter(s -> matchingDoctorIds.contains(s.doctorId().value()))
                .collect(Collectors.toList());

        List<AvailableSlotDto> availableSlots = new ArrayList<>();
        LocalDate fromDate = from != null ? LocalDate.parse(from) : null;
        LocalDate toDate = to != null ? LocalDate.parse(to) : null;

        for (Schedule schedule : schedules) {
            StaffMember doc = doctors.stream()
                    .filter(d -> d.getId().equals(schedule.doctorId().value()))
                    .findFirst().orElse(null);

            if (doc == null) continue;

            for (Slot slot : schedule.slots()) {
                if (!"AVAILABLE".equalsIgnoreCase(slot.getStatus().name())) continue;

                LocalDate slotDate = slot.getTimeRange().startTime().toLocalDate();
                if (fromDate != null && slotDate.isBefore(fromDate)) continue;
                if (toDate != null && slotDate.isAfter(toDate)) continue;

                availableSlots.add(new AvailableSlotDto(
                        slot.getSlotId().value(),
                        doc.getId(),
                        doc.getFirstName(),
                        doc.getLastName(),
                        doc.getSpecialization(),
                        slot.getTimeRange().startTime(),
                        slot.getTimeRange().endTime(),
                        slot.getOffice()
                ));
            }
        }

        return ResponseEntity.ok(availableSlots);
    }
}

record AvailableSlotDto(
        UUID slotId,
        UUID doctorId,
        String doctorFirstName,
        String doctorLastName,
        String specialization,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String office
) {
}
