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
    public ResponseEntity<List<StaffDto>> listDoctors() {
        List<StaffDto> doctors = staffUseCase.getStaffByRole(StaffRole.DOCTOR).stream()
                .map(StaffDto::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctors);
    }
}
