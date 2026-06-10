package pl.MiASI.staff.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.MiASI.staff.application.port.in.CreateStaffCommand;
import pl.MiASI.staff.application.port.in.StaffUseCase;
import pl.MiASI.staff.application.port.in.UpdateStaffCommand;
import pl.MiASI.staff.domain.model.StaffMember;
import pl.MiASI.staff.domain.model.StaffRole;
import pl.MiASI.staff.domain.repository.StaffRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StaffService implements StaffUseCase {

    private final StaffRepository staffRepository;

    @Override
    @Transactional
    public UUID createStaff(CreateStaffCommand command) {
        StaffMember staff = StaffMember.create(
                command.role(),
                command.firstName(),
                command.lastName(),
                command.email(),
                command.specialization(),
                command.pwz(),
                command.department(),
                command.position()
        );
        staffRepository.save(staff);
        return staff.getId();
    }

    @Override
    @Transactional
    public void updateStaff(UUID id, UpdateStaffCommand command) {
        StaffMember staff = staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found"));
        staff.update(
                command.firstName(),
                command.lastName(),
                command.email(),
                command.active(),
                command.specialization(),
                command.pwz(),
                command.department(),
                command.position()
        );
        staffRepository.save(staff);
    }

    @Override
    @Transactional
    public void deactivateStaff(UUID id) {
        StaffMember staff = staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found"));
        staff.deactivate();
        staffRepository.save(staff);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StaffMember> getStaffById(UUID id) {
        return staffRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffMember> getAllStaff() {
        return staffRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffMember> getStaffByRole(StaffRole role) {
        return staffRepository.findByRole(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffMember> searchStaff(String query) {
        if (query == null || query.isBlank()) return getAllStaff();
        String lowerQuery = query.toLowerCase();
        return getAllStaff().stream()
                .filter(s -> s.getFirstName().toLowerCase().contains(lowerQuery) || 
                             s.getLastName().toLowerCase().contains(lowerQuery) || 
                             (s.getSpecialization() != null && s.getSpecialization().toLowerCase().contains(lowerQuery)))
                .toList();
    }
}