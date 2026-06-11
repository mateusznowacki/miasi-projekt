package pl.MiASI.staff.application.port.in;

import pl.MiASI.staff.domain.model.StaffRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStaffCommand(
    @NotNull(message = "Rola jest wymagana") StaffRole role, 
    @NotBlank(message = "Imię jest wymagane") String firstName, 
    @NotBlank(message = "Nazwisko jest wymagane") String lastName, 
    @NotBlank(message = "Email jest wymagany") @Email(message = "Niepoprawny format email") String email, 
    String specialization, 
    @jakarta.validation.constraints.Pattern(regexp = "^[1-9]\\d{6}$", message = "Numer PWZ musi składać się z 7 cyfr") String pwz, 
    String department, 
    String position,
    String workSchedule) {}