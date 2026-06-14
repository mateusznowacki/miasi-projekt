package pl.MiASI.medicalcare.application.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.MiASI.shared.application.domain.model.DoctorId;
import pl.MiASI.shared.application.domain.model.PatientId;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class VisitUnitTest {

    private PatientId mockPatientId() {
        return mock(PatientId.class);
    }

    private DoctorId mockDoctorId() {
        return mock(DoctorId.class);
    }

    @Test
    @DisplayName("Should create reserved visit with valid slots")
    void reserveWhenValidDataShouldReturnReservedVisit() {
        // given
        PatientId patientId = mockPatientId();
        DoctorId doctorId = mockDoctorId();
        ConsultationType type = mock(ConsultationType.class);
        List<SlotId> slotIds = List.of(new SlotId(), new SlotId());

        // when
        Visit visit = Visit.reserve(patientId, doctorId, type, slotIds);

        // then
        assertThat(visit.getVisitId()).isNotNull();
        assertThat(visit.getPatientId()).isEqualTo(patientId);
        assertThat(visit.getDoctorId()).isEqualTo(doctorId);
        assertThat(visit.getConsultationType()).isEqualTo(type);
        assertThat(visit.getSlotIds()).containsExactlyElementsOf(slotIds);
        assertThat(visit.getStatus()).isEqualTo(VisitStatus.RESERVED);
    }

    @Test
    @DisplayName("Should throw exception when reserving visit with null slot list")
    void reserveWhenSlotListIsNullShouldThrowException() {
        // given
        PatientId patientId = mockPatientId();
        DoctorId doctorId = mockDoctorId();
        ConsultationType type = mock(ConsultationType.class);

        // when & then
        assertThatThrownBy(() -> Visit.reserve(patientId, doctorId, type, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Visit must have at least one slot");
    }

    @Test
    @DisplayName("Should throw exception when reserving visit with empty slot list")
    void reserveWhenSlotListIsEmptyShouldThrowException() {
        // given
        PatientId patientId = mockPatientId();
        DoctorId doctorId = mockDoctorId();
        ConsultationType type = mock(ConsultationType.class);

        // when & then
        assertThatThrownBy(() -> Visit.reserve(patientId, doctorId, type, new ArrayList<>()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Visit must have at least one slot");
    }

    @Test
    @DisplayName("Should cancel a reserved visit")
    void cancelWhenVisitIsReservedShouldSetStatusToCanceled() {
        // given
        Visit visit = Visit.reserve(mockPatientId(), mockDoctorId(), mock(ConsultationType.class), List.of(new SlotId()));

        // when
        visit.cancel();

        // then
        assertThat(visit.getStatus()).isEqualTo(VisitStatus.CANCELED);
    }

    @Test
    @DisplayName("Should throw exception when canceling an already canceled visit")
    void cancelWhenVisitIsAlreadyCanceledShouldThrowException() {
        // given
        Visit visit = Visit.reserve(mockPatientId(), mockDoctorId(), mock(ConsultationType.class), List.of(new SlotId()));
        visit.cancel();

        // when & then
        assertThatThrownBy(visit::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only reserved visits can be canceled");
    }

    @Test
    @DisplayName("Should throw exception when canceling a completed visit")
    void cancelWhenVisitIsCompletedShouldThrowException() {
        // given
        Visit visit = Visit.reserve(mockPatientId(), mockDoctorId(), mock(ConsultationType.class), List.of(new SlotId()));
        visit.complete();

        // when & then
        assertThatThrownBy(visit::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only reserved visits can be canceled");
    }

    @Test
    @DisplayName("Should complete a reserved visit")
    void completeWhenVisitIsReservedShouldSetStatusToCompleted() {
        // given
        Visit visit = Visit.reserve(mockPatientId(), mockDoctorId(), mock(ConsultationType.class), List.of(new SlotId()));

        // when
        visit.complete();

        // then
        assertThat(visit.getStatus()).isEqualTo(VisitStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should throw exception when completing an already completed visit")
    void completeWhenVisitIsAlreadyCompletedShouldThrowException() {
        // given
        Visit visit = Visit.reserve(mockPatientId(), mockDoctorId(), mock(ConsultationType.class), List.of(new SlotId()));
        visit.complete();

        // when & then
        assertThatThrownBy(visit::complete)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only reserved visits can be completed");
    }

    @Test
    @DisplayName("Should throw exception when completing a canceled visit")
    void completeWhenVisitIsCanceledShouldThrowException() {
        // given
        Visit visit = Visit.reserve(mockPatientId(), mockDoctorId(), mock(ConsultationType.class), List.of(new SlotId()));
        visit.cancel();

        // when & then
        assertThatThrownBy(visit::complete)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Only reserved visits can be completed");
    }
}
