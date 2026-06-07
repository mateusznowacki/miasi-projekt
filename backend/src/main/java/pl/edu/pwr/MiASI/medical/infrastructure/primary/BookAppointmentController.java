package pl.edu.pwr.MiASI.medical.infrastructure.primary;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwr.MiASI.medical.application.BookAppointmentUseCase;
import pl.edu.pwr.MiASI.medical.domain.PatientId;
import pl.edu.pwr.MiASI.medical.domain.SlotId;
import pl.edu.pwr.MiASI.medical.domain.ConsultationType;
import pl.edu.pwr.MiASI.staff.domain.DoctorId;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
public class BookAppointmentController {
    private final BookAppointmentUseCase rezerwacjaWizytyUseCase;

    public BookAppointmentController(BookAppointmentUseCase rezerwacjaWizytyUseCase) {
        this.rezerwacjaWizytyUseCase = rezerwacjaWizytyUseCase;
    }

    @PostMapping("/rezerwuj")
    public ResponseEntity<Void> rezerwujWizyte(@RequestBody RezerwacjaRequest request) {
        PatientId patientId = new PatientId(request.patientId());
        DoctorId lekarzId = new DoctorId(request.lekarzId());
        ConsultationType typ = ConsultationType.valueOf(request.consultationType());
        List<SlotId> slots = request.selectedSlots().stream().map(SlotId::new).collect(Collectors.toList());

        rezerwacjaWizytyUseCase.execute(patientId, lekarzId, typ, slots);

        return ResponseEntity.ok().build();
    }
}

record RezerwacjaRequest(UUID patientId, UUID lekarzId, String consultationType, List<UUID> selectedSlots) {}
