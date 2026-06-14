package pl.MiASI.medicalcare.infrastructure.in.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.MiASI.medicalcare.application.domain.event.RecordCreatedEvent;
import pl.MiASI.medicalcare.application.domain.event.VisitCanceledEvent;
import pl.MiASI.medicalcare.application.domain.event.VisitReservedEvent;
import pl.MiASI.medicalcare.application.domain.model.ConsultationType;
import pl.MiASI.medicalcare.application.domain.model.SlotId;
import pl.MiASI.medicalcare.application.domain.model.Visit;
import pl.MiASI.medicalcare.application.domain.model.VisitId;
import pl.MiASI.medicalcare.application.port.in.ScheduleManagementUseCase;
import pl.MiASI.medicalcare.application.port.in.VisitManagementUseCase;
import pl.MiASI.medicalcare.application.port.out.VisitRepository;
import pl.MiASI.shared.application.domain.model.DoctorId;
import pl.MiASI.shared.application.domain.model.PatientId;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitEventListenerUnitTest {

    @Mock
    private ScheduleManagementUseCase scheduleManagementUseCase;

    @Mock
    private VisitManagementUseCase visitManagementUseCase;

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitEventListener listener;

    @Test
    @DisplayName("Should free slots when VisitCanceledEvent is received and visit exists")
    void onVisitCanceledWhenVisitExistsShouldFreeSlots() {
        // given
        VisitId visitId = new VisitId();
        List<SlotId> slotIds = List.of(new SlotId());
        VisitCanceledEvent event = new VisitCanceledEvent(visitId, slotIds);
        
        DoctorId doctorId = mock(DoctorId.class);
        PatientId patientId = mock(PatientId.class);
        Visit visit = Visit.reserve(patientId, doctorId, ConsultationType.GENERAL, slotIds);
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));

        // when
        listener.onVisitCanceled(event);

        // then
        verify(scheduleManagementUseCase).freeSlots(doctorId, slotIds);
    }

    @Test
    @DisplayName("Should not free slots when VisitCanceledEvent is received but visit not found")
    void onVisitCanceledWhenVisitNotFoundShouldDoNothing() {
        // given
        VisitId visitId = new VisitId();
        VisitCanceledEvent event = new VisitCanceledEvent(visitId, List.of(new SlotId()));
        when(visitRepository.findById(visitId)).thenReturn(Optional.empty());

        // when
        listener.onVisitCanceled(event);

        // then
        verifyNoInteractions(scheduleManagementUseCase);
    }

    @Test
    @DisplayName("Should complete visit when RecordCreatedEvent is received")
    void onRecordCreatedShouldCompleteVisit() {
        // given
        VisitId visitId = new VisitId();
        RecordCreatedEvent event = new RecordCreatedEvent(visitId);

        // when
        listener.onRecordCreated(event);

        // then
        verify(visitManagementUseCase).completeVisit(visitId);
    }

    @Test
    @DisplayName("Should log confirmation when VisitReservedEvent is received")
    void onVisitReservedShouldLogConfirmation() {
        // given
        VisitId visitId = new VisitId();
        VisitReservedEvent event = new VisitReservedEvent(visitId);
        
        DoctorId doctorId = mock(DoctorId.class);
        PatientId patientId = mock(PatientId.class);
        Visit visit = Visit.reserve(patientId, doctorId, ConsultationType.GENERAL, List.of(new SlotId()));
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));

        // when
        listener.onVisitReserved(event);

        // then
        // Since it only does System.out.println, we just verify no exceptions occur and repository was called
        verify(visitRepository).findById(visitId);
    }
}
