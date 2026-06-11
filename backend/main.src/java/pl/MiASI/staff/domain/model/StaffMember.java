package pl.MiASI.staff.domain.model;

import lombok.Getter;
import java.util.UUID;

@Getter
public class StaffMember {
    private final UUID id;
    private final StaffRole role;
    private String firstName;
    private String lastName;
    private String email;
    private boolean active;
    private String specialization;
    private String pwz;
    private String department;
    private String position;
    private String workSchedule;

    public StaffMember(UUID id, StaffRole role, String firstName, String lastName, String email, boolean active, String specialization, String pwz, String department, String position, String workSchedule) {
        this.id = id;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
        this.specialization = specialization;
        this.pwz = pwz;
        this.department = department;
        this.position = position;
        this.workSchedule = workSchedule;
    }

    public static StaffMember create(UUID id, StaffRole role, String firstName, String lastName, String email, String specialization, String pwz, String department, String position, String workSchedule) {
        return new StaffMember(id, role, firstName, lastName, email, true, specialization, pwz, department, position, workSchedule);
    }

    public void update(String firstName, String lastName, String email, boolean active, String specialization, String pwz, String department, String position, String workSchedule) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
        this.specialization = specialization;
        this.pwz = pwz;
        this.department = department;
        this.position = position;
        this.workSchedule = workSchedule;
    }

    public void deactivate() {
        this.active = false;
    }
}