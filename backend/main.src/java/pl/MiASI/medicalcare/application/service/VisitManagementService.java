package pl.MiASI.medicalcare.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.MiASI.shared.domain.model.DoctorId;
import pl.MiASI.shared.domain.model.PatientId;
import pl.MiASI.medicalcare.application.port.in.VisitManagementUseCase;
import pl.MiASI.medicalcare.domain.event.VisitCanceledEvent;
import pl.MiASI.medicalcare.domain.event.VisitCompletedEvent;
import pl.MiASI.medicalcare.domain.event.VisitReservedEvent;
import pl.MiASI.medicalcare.domain.model.*;
import pl.MiASI.medicalcare.domain.repository.ScheduleRepository;
import pl.MiASI.medicalcare.domain.repository.VisitRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitManagementService implements VisitManagementUseCase {

    private final VisitRepository visitRepository;
    private final ScheduleRepository scheduleRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public VisitId reserveVisit(PatientId patientId, DoctorId doctorId, ConsultationType type, List<SlotId> slotIds) {
        Schedule schedule = scheduleRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found for doctor"));

        schedule.reserveSlots(slotIds);

        Visit visit = Visit.reserve(patientId, doctorId, type, slotIds);

        scheduleRepository.save(schedule);
        visitRepository.save(visit);

        eventPublisher.publishEvent(new VisitReservedEvent(visit.getVisitId()));

        return visit.getVisitId();
    }

    @Override
    @Transactional
    public void cancelVisit(VisitId visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found"));

        visit.cancel();
        visitRepository.save(visit);

        eventPublisher.publishEvent(new VisitCanceledEvent(visit.getVisitId(), visit.getSlotIds()));
    }

    @Override
    @Transactional
    public void completeVisit(VisitId visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found"));

        visit.complete();
        visitRepository.save(visit);

        eventPublisher.publishEvent(new VisitCompletedEvent(visit.getVisitId()));
    }
}