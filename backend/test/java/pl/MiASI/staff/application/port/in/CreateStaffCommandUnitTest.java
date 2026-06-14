package pl.MiASI.staff.application.port.in;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.MiASI.staff.application.domain.model.StaffRole;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreateStaffCommandUnitTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("CreateStaffCommand when all fields are valid should pass validation")
    void createStaffCommandWhenValidDataShouldPassValidation() {
        // given
        CreateStaffCommand command = new CreateStaffCommand(
                StaffRole.DOCTOR, "John", "Doe", "john@test.com",
                "Cardiology", "1234567", "Dept", "Doc", "9-17"
        );

        // when
        Set<ConstraintViolation<CreateStaffCommand>> violations = validator.validate(command);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("CreateStaffCommand when role is null should fail validation")
    void createStaffCommandWhenRoleIsNullShouldFailValidation() {
        // given
        CreateStaffCommand command = new CreateStaffCommand(
                null, "John", "Doe", "john@test.com",
                "Cardiology", "1234567", "Dept", "Doc", "9-17"
        );

        // when
        Set<ConstraintViolation<CreateStaffCommand>> violations = validator.validate(command);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Rola jest wymagana");
    }

    @Test
    @DisplayName("CreateStaffCommand when first name is blank should fail validation")
    void createStaffCommandWhenFirstNameIsBlankShouldFailValidation() {
        // given
        CreateStaffCommand command = new CreateStaffCommand(
                StaffRole.DOCTOR, "", "Doe", "john@test.com",
                "Cardiology", "1234567", "Dept", "Doc", "9-17"
        );

        // when
        Set<ConstraintViolation<CreateStaffCommand>> violations = validator.validate(command);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Imię jest wymagane");
    }

    @Test
    @DisplayName("CreateStaffCommand when last name is blank should fail validation")
    void createStaffCommandWhenLastNameIsBlankShouldFailValidation() {
        // given
        CreateStaffCommand command = new CreateStaffCommand(
                StaffRole.DOCTOR, "John", "", "john@test.com",
                "Cardiology", "1234567", "Dept", "Doc", "9-17"
        );

        // when
        Set<ConstraintViolation<CreateStaffCommand>> violations = validator.validate(command);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Nazwisko jest wymagane");
    }

    @Test
    @DisplayName("CreateStaffCommand when email is invalid should fail validation")
    void createStaffCommandWhenEmailIsInvalidShouldFailValidation() {
        // given
        CreateStaffCommand command = new CreateStaffCommand(
                StaffRole.DOCTOR, "John", "Doe", "invalid-email",
                "Cardiology", "1234567", "Dept", "Doc", "9-17"
        );

        // when
        Set<ConstraintViolation<CreateStaffCommand>> violations = validator.validate(command);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Niepoprawny format email");
    }

    @Test
    @DisplayName("CreateStaffCommand when PWZ is invalid format should fail validation")
    void createStaffCommandWhenPwzIsInvalidShouldFailValidation() {
        // given
        CreateStaffCommand command = new CreateStaffCommand(
                StaffRole.DOCTOR, "John", "Doe", "john@test.com",
                "Cardiology", "123456", "Dept", "Doc", "9-17"
        );

        // when
        Set<ConstraintViolation<CreateStaffCommand>> violations = validator.validate(command);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Numer PWZ musi składać się z 7 cyfr");
    }

    @Test
    @DisplayName("CreateStaffCommand when PWZ starts with zero should fail validation")
    void createStaffCommandWhenPwzStartsWithZeroShouldFailValidation() {
        // given
        CreateStaffCommand command = new CreateStaffCommand(
                StaffRole.DOCTOR, "John", "Doe", "john@test.com",
                "Cardiology", "0123456", "Dept", "Doc", "9-17"
        );

        // when
        Set<ConstraintViolation<CreateStaffCommand>> violations = validator.validate(command);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Numer PWZ musi składać się z 7 cyfr");
    }
}
