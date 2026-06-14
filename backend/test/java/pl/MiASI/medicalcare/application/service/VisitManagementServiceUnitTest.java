package pl.MiASI.medicalcare.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import pl.MiASI.medicalcare.application.domain.event.VisitCanceledEvent;
import pl.MiASI.medicalcare.application.domain.event.VisitCompletedEvent;
import pl.MiASI.medicalcare.application.domain.event.VisitReservedEvent;
import pl.MiASI.medicalcare.application.domain.model.*;
import pl.MiASI.medicalcare.application.port.in.AddSlotCommand;
import pl.MiASI.medicalcare.application.port.out.ScheduleRepository;
import pl.MiASI.medicalcare.application.port.out.VisitRepository;
import pl.MiASI.shared.application.domain.model.DoctorId;
import pl.MiASI.shared.application.domain.model.PatientId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitManagementServiceUnitTest {

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private VisitManagementService service;

    private PatientId mockPatientId() {
        return mock(PatientId.class);
    }

    private DoctorId mockDoctorId() {
        return mock(DoctorId.class);
    }

    @Test
    @DisplayName("Should reserve visit and publish event when valid data provided")
    void reserveVisitWhenValidDataShouldSaveAndPublishEvent() {
        // given
        PatientId patientId = mockPatientId();
        DoctorId doctorId = mockDoctorId();
        ConsultationType type = ConsultationType.GENERAL;
        
        Schedule schedule = Schedule.create(doctorId);
        schedule.addTimeSlots(List.of(new AddSlotCommand(
                new TimeRange(LocalDateTime.of(2023, 10, 10, 10, 0), LocalDateTime.of(2023, 10, 10, 11, 0)),
                "Room A"
        )));
        SlotId slotId = schedule.slots().get(0).getSlotId();
        List<SlotId> slotIds = List.of(slotId);

        when(scheduleRepository.findByDoctorId(doctorId)).thenReturn(Optional.of(schedule));

        // when
        VisitId visitId = service.reserveVisit(patientId, doctorId, type, slotIds);

        // then
        assertThat(visitId).isNotNull();
        verify(scheduleRepository).save(schedule);
        assertThat(schedule.slots().get(0).getStatus()).isEqualTo(SlotStatus.BOOKED);
        
        ArgumentCaptor<Visit> visitCaptor = ArgumentCaptor.forClass(Visit.class);
        verify(visitRepository).save(visitCaptor.capture());
        assertThat(visitCaptor.getValue().getVisitId()).isEqualTo(visitId);
        
        ArgumentCaptor<VisitReservedEvent> eventCaptor = ArgumentCaptor.forClass(VisitReservedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().visitId()).isEqualTo(visitId);
    }

    @Test
    @DisplayName("Should throw exception when reserving visit and schedule not found")
    void reserveVisitWhenScheduleNotFoundShouldThrowException() {
        // given
        DoctorId doctorId = mockDoctorId();
        when(scheduleRepository.findByDoctorId(doctorId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.reserveVisit(mockPatientId(), doctorId, ConsultationType.GENERAL, List.of(new SlotId())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Schedule not found for doctor");
        
        verify(visitRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when reserving visit for already booked slot")
    void reserveVisitWhenSlotAlreadyBookedShouldThrowException() {
        // given
        DoctorId doctorId = mockDoctorId();
        Schedule schedule = Schedule.create(doctorId);
        schedule.addTimeSlots(List.of(new AddSlotCommand(
                new TimeRange(LocalDateTime.of(2023, 10, 10, 10, 0), LocalDateTime.of(2023, 10, 10, 11, 0)),
                "Room A"
        )));
        SlotId slotId = schedule.slots().get(0).getSlotId();
        schedule.reserveSlots(List.of(slotId)); // already booked
        
        when(scheduleRepository.findByDoctorId(doctorId)).thenReturn(Optional.of(schedule));

        // when & then
        assertThatThrownBy(() -> service.reserveVisit(mockPatientId(), doctorId, ConsultationType.GENERAL, List.of(slotId)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Slot is already booked");
                
        verify(visitRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should cancel visit and publish event when visit exists")
    void cancelVisitWhenVisitExistsShouldCancelAndPublishEvent() {
        // given
        Visit visit = Visit.reserve(mockPatientId(), mockDoctorId(), ConsultationType.GENERAL, List.of(new SlotId()));
        VisitId visitId = visit.getVisitId();
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));

        // when
        service.cancelVisit(visitId);

        // then
        verify(visitRepository).save(visit);
        assertThat(visit.getStatus()).isEqualTo(VisitStatus.CANCELED);
        
        ArgumentCaptor<VisitCanceledEvent> eventCaptor = ArgumentCaptor.forClass(VisitCanceledEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().visitId()).isEqualTo(visitId);
    }

    @Test
    @DisplayName("Should throw exception when canceling non-existent visit")
    void cancelVisitWhenVisitNotFoundShouldThrowException() {
        // given
        VisitId visitId = new VisitId();
        when(visitRepository.findById(visitId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.cancelVisit(visitId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Visit not found");
    }

    @Test
    @DisplayName("Should complete visit and publish event when visit exists")
    void completeVisitWhenVisitExistsShouldCompleteAndPublishEvent() {
        // given
        Visit visit = Visit.reserve(mockPatientId(), mockDoctorId(), ConsultationType.GENERAL, List.of(new SlotId()));
        VisitId visitId = visit.getVisitId();
        when(visitRepository.findById(visitId)).thenReturn(Optional.of(visit));

        // when
        service.completeVisit(visitId);

        // then
        verify(visitRepository).save(visit);
        assertThat(visit.getStatus()).isEqualTo(VisitStatus.COMPLETED);
        
        ArgumentCaptor<VisitCompletedEvent> eventCaptor = ArgumentCaptor.forClass(VisitCompletedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().visitId()).isEqualTo(visitId);
    }

    @Test
    @DisplayName("Should throw exception when completing non-existent visit")
    void completeVisitWhenVisitNotFoundShouldThrowException() {
        // given
        VisitId visitId = new VisitId();
        when(visitRepository.findById(visitId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.completeVisit(visitId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Visit not found");
    }
}
