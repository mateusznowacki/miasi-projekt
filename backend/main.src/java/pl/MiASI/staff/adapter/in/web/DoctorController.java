package pl.MiASI.staff.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.MiASI.staff.application.port.in.StaffUseCase;
import pl.MiASI.staff.domain.model.StaffRole;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final StaffUseCase staffUseCase;

    @GetMapping
    public ResponseEntity<List<StaffDto>> listDoctors(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String specialization,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String name) {
        List<StaffDto> doctors = staffUseCase.getStaffByRole(StaffRole.DOCTOR).stream()
                .filter(d -> specialization == null || (d.getSpecialization() != null && d.getSpecialization().equalsIgnoreCase(specialization)))
                .filter(d -> name == null || d.getFirstName().toLowerCase().contains(name.toLowerCase()) || d.getLastName().toLowerCase().contains(name.toLowerCase()))
                .map(StaffDto::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctors);
    }
}
