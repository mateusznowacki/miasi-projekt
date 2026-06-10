package pl.MiASI.staff.application.port.in;

public record UpdateStaffCommand(String firstName, String lastName, String email, boolean active, String specialization, String pwz, String department, String position) {}