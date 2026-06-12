package pl.MiASI.patient.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import pl.MiASI.iam.application.port.in.AuthUseCase;
import pl.MiASI.patient.domain.model.Patient;
import pl.MiASI.patient.domain.repository.PatientRepository;
import pl.MiASI.shared.domain.model.PatientId;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceSearchTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AuthUseCase authUseCase;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PatientService patientService;

    private Patient janKowalski;
    private Patient annaWisniewska;

    @BeforeEach
    void setUp() {
        janKowalski = Patient.create(
                new PatientId(UUID.randomUUID()),
                "Jan",
                "Kowalski",
                "90010112345",
                "600 100 200",
                "pacjent@medflow.pl"
        );
        annaWisniewska = Patient.create(
                new PatientId(UUID.randomUUID()),
                "Anna",
                "Wiśniewska",
                "85052254321",
                "601 222 333",
                "anna.wisniewska@medflow.pl"
        );
        when(patientRepository.findAll()).thenReturn(List.of(janKowalski, annaWisniewska));
    }

    @Test
    void shouldFindPatientByFirstNameOnly() {
        var results = patientService.searchPatients(null, "Jan", null, null);

        assertThat(results).containsExactly(janKowalski);
    }

    @Test
    void shouldFindPatientByLastNameFragment() {
        var results = patientService.searchPatients(null, "Kow", null, null);

        assertThat(results).containsExactly(janKowalski);
    }

    @Test
    void shouldFindPatientByFirstAndLastName() {
        var results = patientService.searchPatients("Anna", "Wiśniewska", null, null);

        assertThat(results).containsExactly(annaWisniewska);
    }

    @Test
    void shouldFindPatientByPesel() {
        var results = patientService.searchPatients(null, null, "850522", null);

        assertThat(results).containsExactly(annaWisniewska);
    }
}
