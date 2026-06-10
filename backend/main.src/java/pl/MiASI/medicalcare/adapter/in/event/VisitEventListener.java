package pl.MiASI.medicalcare.adapter.in.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.MiASI.medicalcare.application.port.in.ScheduleManagementUseCase;
import pl.MiASI.medicalcare.application.port.in.VisitManagementUseCase;
import pl.MiASI.medicalcare.domain.event.RecordCreatedEvent;
import pl.MiASI.medicalcare.domain.event.VisitCanceledEvent;
import pl.MiASI.medicalcare.domain.model.Visit;
import pl.MiASI.medicalcare.domain.repository.VisitRepository;

@Component
@RequiredArgsConstructor
public class VisitEventListener {

    private final ScheduleManagementUseCase scheduleManagementUseCase;
    private final VisitManagementUseCase visitManagementUseCase;
    private final VisitRepository visitRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onVisitCanceled(VisitCanceledEvent event) {
        // Need to find doctorId from visit to free slots.
        // Wait, event only has visitId and slotIds.
        visitRepository.findById(event.visitId()).ifPresent(visit -> {
            scheduleManagementUseCase.freeSlots(visit.getDoctorId(), event.slotIds());
        });
    }

    @EventListener
    public void onRecordCreated(RecordCreatedEvent event) {
        visitManagementUseCase.completeVisit(event.visitId());
    }
}