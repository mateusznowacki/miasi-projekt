package pl.MiASI.medicalcare.infrastructure.in.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.MiASI.medicalcare.application.domain.event.RecordCreatedEvent;
import pl.MiASI.medicalcare.application.domain.event.VisitCanceledEvent;
import pl.MiASI.medicalcare.application.port.in.ScheduleManagementUseCase;
import pl.MiASI.medicalcare.application.port.in.VisitManagementUseCase;
import pl.MiASI.medicalcare.application.port.out.VisitRepository;

@Component
@RequiredArgsConstructor
class VisitEventListener {

    private final ScheduleManagementUseCase scheduleManagementUseCase;
    private final VisitManagementUseCase visitManagementUseCase;
    private final VisitRepository visitRepository;

    @EventListener
    public void onVisitCanceled(VisitCanceledEvent event) {
        visitRepository.findById(event.visitId()).ifPresent(visit -> {
            scheduleManagementUseCase.freeSlots(visit.getDoctorId(), event.slotIds());
            System.out.println("[Powiadomienie] Wysyłanie powiadomienia o anulowaniu wizyty " + event.visitId().value() + " do lekarza: " + visit.getDoctorId().value());
        });
    }

    @EventListener
    public void onRecordCreated(RecordCreatedEvent event) {
        visitManagementUseCase.completeVisit(event.visitId());
    }

    @EventListener
    public void onVisitReserved(pl.MiASI.medicalcare.application.domain.event.VisitReservedEvent event) {
        visitRepository.findById(event.visitId()).ifPresent(visit -> {
            System.out.println("[Powiadomienie] Wysyłanie potwierdzenia o rezerwacji wizyty " + event.visitId().value() + " do pacjenta: " + visit.getPatientId().value());
        });
    }
}