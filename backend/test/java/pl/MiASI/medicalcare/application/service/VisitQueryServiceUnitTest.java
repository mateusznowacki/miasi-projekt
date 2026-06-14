package pl.MiASI.medicalcare.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.MiASI.medicalcare.application.domain.model.ConsultationType;
import pl.MiASI.medicalcare.application.domain.model.SlotId;
import pl.MiASI.medicalcare.application.domain.model.Visit;
import pl.MiASI.medicalcare.application.port.out.VisitRepository;
import pl.MiASI.shared.application.domain.model.DoctorId;
import pl.MiASI.shared.application.domain.model.PatientId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitQueryServiceUnitTest {

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitQueryService service;

    private PatientId mockPatientId() {
        return mock(PatientId.class);
    }

    private DoctorId mockDoctorId() {
        return mock(DoctorId.class);
    }

    @Test
    @DisplayName("Should return visits by patient ID when they exist")
    void getVisitsByPatientWhenExistsShouldReturnList() {
        // given
        PatientId patientId = mockPatientId();
        Visit visit = Visit.reserve(patientId, mockDoctorId(), ConsultationType.GENERAL, List.of(new SlotId()));
        when(visitRepository.findByPatientId(patientId)).thenReturn(List.of(visit));

        // when
        List<Visit> result = service.getVisitsByPatient(patientId);

        // then
        assertThat(result).hasSize(1).containsExactly(visit);
    }

    @Test
    @DisplayName("Should return empty list when no visits for patient exist")
    void getVisitsByPatientWhenNoneShouldReturnEmptyList() {
        // given
        PatientId patientId = mockPatientId();
        when(visitRepository.findByPatientId(patientId)).thenReturn(List.of());

        // when
        List<Visit> result = service.getVisitsByPatient(patientId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return visits by doctor ID when they exist")
    void getVisitsByDoctorWhenExistsShouldReturnList() {
        // given
        DoctorId doctorId = mockDoctorId();
        Visit visit = Visit.reserve(mockPatientId(), doctorId, ConsultationType.GENERAL, List.of(new SlotId()));
        when(visitRepository.findByDoctorId(doctorId)).thenReturn(List.of(visit));

        // when
        List<Visit> result = service.getVisitsByDoctor(doctorId);

        // then
        assertThat(result).hasSize(1).containsExactly(visit);
    }

    @Test
    @DisplayName("Should return empty list when no visits for doctor exist")
    void getVisitsByDoctorWhenNoneShouldReturnEmptyList() {
        // given
        DoctorId doctorId = mockDoctorId();
        when(visitRepository.findByDoctorId(doctorId)).thenReturn(List.of());

        // when
        List<Visit> result = service.getVisitsByDoctor(doctorId);

        // then
        assertThat(result).isEmpty();
    }
}
