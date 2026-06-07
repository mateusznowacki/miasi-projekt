package pl.edu.pwr.MiASI.medical.application;

import org.springframework.stereotype.Service;

import pl.edu.pwr.MiASI.medical.domain.*;
import pl.edu.pwr.MiASI.shared.domain.DomainEventPublisher;

@Service
public class FillMedicalRecordUseCase {
    private final MedicalRecordRepository rekordMedycznyRepository;
    private final DomainEventPublisher eventPublisher;

    public FillMedicalRecordUseCase(MedicalRecordRepository rekordMedycznyRepository, DomainEventPublisher eventPublisher) {
        this.rekordMedycznyRepository = rekordMedycznyRepository;
        this.eventPublisher = eventPublisher;
    }

    public void execute(AppointmentId wizytaId, Diagnosis diagnosis, Symptom symptom, Prescription prescription, String notes, PatientId patientId, pl.edu.pwr.MiASI.staff.domain.DoctorId lekarzId) {
        MedicalRecord rekord = MedicalRecord.create(wizytaId, patientId, lekarzId, diagnosis, symptom, prescription, notes);
        
        rekordMedycznyRepository.save(rekord);
        
        eventPublisher.publish(new MedicalRecordCreated(rekord.getId(), wizytaId));
    }
}
