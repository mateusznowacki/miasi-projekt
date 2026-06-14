package pl.MiASI.medicalcare.infrastructure.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.MiASI.medicalcare.application.domain.model.Schedule;
import pl.MiASI.medicalcare.application.domain.model.SlotId;
import pl.MiASI.medicalcare.application.port.in.AddSlotCommand;
import pl.MiASI.medicalcare.application.port.in.ScheduleManagementUseCase;
import pl.MiASI.medicalcare.application.port.in.ScheduleQueryUseCase;
import pl.MiASI.shared.application.domain.model.DoctorId;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
class ScheduleController {

    private final ScheduleManagementUseCase scheduleManagementUseCase;
    private final ScheduleQueryUseCase scheduleQueryUseCase;

    @PostMapping("/{doctorId}/slots")
    public ResponseEntity<Void> addTimeSlots(@PathVariable UUID doctorId, @RequestBody AddSlotsRequest request) {
        scheduleManagementUseCase.addTimeSlots(new DoctorId(doctorId), request.commands());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{doctorId}/slots/{slotId}")
    public ResponseEntity<Void> updateSlot(@PathVariable UUID doctorId, @PathVariable UUID slotId, @RequestBody UpdateSlotRequest request) {
        scheduleManagementUseCase.updateSlot(new DoctorId(doctorId), new SlotId(slotId), request.timeRange(), request.office());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{doctorId}/slots/{slotId}")
    public ResponseEntity<Void> removeSlot(@PathVariable UUID doctorId, @PathVariable UUID slotId) {
        scheduleManagementUseCase.removeSlot(new DoctorId(doctorId), new SlotId(slotId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ScheduleDto> getScheduleByDoctor(
            @PathVariable UUID doctorId,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String status) {
        return scheduleQueryUseCase.getScheduleByDoctor(new DoctorId(doctorId))
                .map(schedule -> {
                    List<pl.MiASI.medicalcare.application.domain.model.Slot> filteredSlots = schedule.slots();
                    if (date != null) {
                        java.time.LocalDate targetDate = parseDateParam(date);
                        filteredSlots = filteredSlots.stream()
                                .filter(s -> s.getTimeRange().startTime().toLocalDate().equals(targetDate))
                                .collect(Collectors.toList());
                    }
                    if (from != null && to != null) {
                        java.time.LocalDate fromDate = parseDateParam(from);
                        java.time.LocalDate toDate = parseDateParam(to);
                        filteredSlots = filteredSlots.stream()
                                .filter(s -> !s.getTimeRange().startTime().toLocalDate().isBefore(fromDate) &&
                                        !s.getTimeRange().startTime().toLocalDate().isAfter(toDate))
                                .collect(Collectors.toList());
                    }
                    if (status != null) {
                        filteredSlots = filteredSlots.stream()
                                .filter(s -> s.getStatus().name().equalsIgnoreCase(status))
                                .collect(Collectors.toList());
                    }
                    return ScheduleDto.fromDomain(new Schedule(schedule.scheduleId(), schedule.doctorId(), filteredSlots));
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private static java.time.LocalDate parseDateParam(String value) {
        String datePart = value.length() >= 10 ? value.substring(0, 10) : value;
        return java.time.LocalDate.parse(datePart);
    }
}

record AddSlotsRequest(List<AddSlotCommand> commands) {
}

record UpdateSlotRequest(pl.MiASI.medicalcare.application.domain.model.TimeRange timeRange, String office) {
}

record SlotDto(UUID slotId, pl.MiASI.medicalcare.application.domain.model.TimeRange timeRange, String office, String status) {
}

record ScheduleDto(UUID scheduleId, UUID doctorId, List<SlotDto> slots) {
    static ScheduleDto fromDomain(Schedule schedule) {
        List<SlotDto> slotDtos = schedule.slots().stream()
                .map(s -> new SlotDto(s.getSlotId().value(), s.getTimeRange(), s.getOffice(), s.getStatus().name()))
                .collect(Collectors.toList());
        return new ScheduleDto(schedule.scheduleId().value(), schedule.doctorId().value(), slotDtos);
    }
}
