package pl.edu.pwr.MiASI.medical.infrastructure.primary;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwr.MiASI.medical.domain.ScheduleRepository;
import pl.edu.pwr.MiASI.medical.domain.Schedule;
import pl.edu.pwr.MiASI.staff.domain.DoctorId;

import java.util.UUID;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
    private final ScheduleRepository harmonogramRepository;

    public ScheduleController(ScheduleRepository harmonogramRepository) {
        this.harmonogramRepository = harmonogramRepository;
    }

    @GetMapping("/doctor/{lekarzId}")
    public ResponseEntity<Schedule> getSchedule(@PathVariable UUID lekarzId) {
        return harmonogramRepository.findByLekarzId(new DoctorId(lekarzId))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
