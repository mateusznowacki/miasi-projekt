package pl.MiASI.staff.application.port.in;

import pl.MiASI.staff.domain.model.StaffRole;

public record CreateStaffCommand(StaffRole role, String firstName, String lastName, String email, String specialization, String pwz, String department, String position) {}