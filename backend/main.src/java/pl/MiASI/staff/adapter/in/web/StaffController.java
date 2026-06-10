package pl.MiASI.staff.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.MiASI.staff.application.port.in.CreateStaffCommand;
import pl.MiASI.staff.application.port.in.StaffUseCase;
import pl.MiASI.staff.application.port.in.UpdateStaffCommand;
import pl.MiASI.staff.domain.model.StaffMember;
import pl.MiASI.staff.domain.model.StaffRole;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffUseCase staffUseCase;

    @GetMapping
    public ResponseEntity<List<StaffDto>> listStaff(@RequestParam(required = false) String role) {
        List<StaffMember> staffMembers;
        if (role != null) {
            staffMembers = staffUseCase.getStaffByRole(StaffRole.valueOf(role.toUpperCase()));
        } else {
            staffMembers = staffUseCase.getAllStaff();
        }
        return ResponseEntity.ok(staffMembers.stream().map(StaffDto::fromDomain).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createStaff(@RequestBody CreateStaffCommand command) {
        UUID staffId = staffUseCase.createStaff(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("staffId", staffId.toString()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffDto> getStaff(@PathVariable UUID id) {
        return staffUseCase.getStaffById(id)
                .map(StaffDto::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateStaff(@PathVariable UUID id, @RequestBody UpdateStaffCommand command) {
        staffUseCase.updateStaff(id, command);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateStaff(@PathVariable UUID id) {
        staffUseCase.deactivateStaff(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<StaffDto>> searchStaff(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(staffUseCase.searchStaff(query).stream().map(StaffDto::fromDomain).collect(Collectors.toList()));
    }
}

record StaffDto(UUID id, String role, String firstName, String lastName, String email, boolean active, String specialization, String pwz, String department, String position) {
    static StaffDto fromDomain(StaffMember staff) {
        return new StaffDto(
                staff.getId(),
                staff.getRole().name(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getEmail(),
                staff.isActive(),
                staff.getSpecialization(),
                staff.getPwz(),
                staff.getDepartment(),
                staff.getPosition()
        );
    }
}
