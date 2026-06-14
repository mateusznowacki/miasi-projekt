package pl.MiASI.staff.application.port.in;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateStaffCommandUnitTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("UpdateStaffCommand when all fields are valid should pass validation")
    void updateStaffCommandWhenValidDataShouldPassValidation() {
        // given
        UpdateStaffCommand command = new UpdateStaffCommand(
                "John", "Doe", "john@test.com", true,
                "Cardiology", "1234567", "Dept", "Doc", "9-17"
        );

        // when
        Set<ConstraintViolation<UpdateStaffCommand>> violations = validator.validate(command);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("UpdateStaffCommand when first name is blank should fail validation")
    void updateStaffCommandWhenFirstNameIsBlankShouldFailValidation() {
        // given
        UpdateStaffCommand command = new UpdateStaffCommand(
                "", "Doe", "john@test.com", true,
                "Cardiology", "1234567", "Dept", "Doc", "9-17"
        );

        // when
        Set<ConstraintViolation<UpdateStaffCommand>> violations = validator.validate(command);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Imię jest wymagane");
    }

    @Test
    @DisplayName("UpdateStaffCommand when last name is blank should fail validation")
    void updateStaffCommandWhenLastNameIsBlankShouldFailValidation() {
        // given
        UpdateStaffCommand command = new UpdateStaffCommand(
                "John", "", "john@test.com", true,
                "Cardiology", "1234567", "Dept", "Doc", "9-17"
        );

        // when
        Set<ConstraintViolation<UpdateStaffCommand>> violations = validator.validate(command);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Nazwisko jest wymagane");
    }

    @Test
    @DisplayName("UpdateStaffCommand when email is invalid should fail validation")
    void updateStaffCommandWhenEmailIsInvalidShouldFailValidation() {
        // given
        UpdateStaffCommand command = new UpdateStaffCommand(
                "John", "Doe", "not-an-email", true,
                "Cardiology", "1234567", "Dept", "Doc", "9-17"
        );

        // when
        Set<ConstraintViolation<UpdateStaffCommand>> violations = validator.validate(command);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Niepoprawny format email");
    }

    @Test
    @DisplayName("UpdateStaffCommand when PWZ is invalid format should fail validation")
    void updateStaffCommandWhenPwzIsInvalidShouldFailValidation() {
        // given
        UpdateStaffCommand command = new UpdateStaffCommand(
                "John", "Doe", "john@test.com", true,
                "Cardiology", "123456789", "Dept", "Doc", "9-17"
        );

        // when
        Set<ConstraintViolation<UpdateStaffCommand>> violations = validator.validate(command);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Numer PWZ musi składać się z 7 cyfr");
    }
}
