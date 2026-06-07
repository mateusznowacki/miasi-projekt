package pl.edu.pwr.MiASI.medical.infrastructure;

import org.springframework.stereotype.Component;
import pl.edu.pwr.MiASI.medical.domain.*;
import pl.edu.pwr.MiASI.staff.domain.DoctorId;
import java.util.Optional;

@Component
public class MedicalRecordRepositoryAdapter implements MedicalRecordRepository {
    private final SpringDataMedicalRecordRepository repository;

    public MedicalRecordRepositoryAdapter(SpringDataMedicalRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(MedicalRecord rekord) {
        MedicalRecordJpaEntity entity = new MedicalRecordJpaEntity(
            rekord.getId().id(),
            rekord.getWizytaId().id(),
            rekord.getPacjentId().id(),
            rekord.getLekarzId().id(),
            rekord.getDiagnoza().kodICD10(),
            rekord.getDiagnoza().description(),
            rekord.getObjaw().description(),
            rekord.getRecepta().medication(),
            rekord.getRecepta().dosage(),
            rekord.getNotatki()
        );
        repository.save(entity);
    }

    @Override
    public Optional<MedicalRecord> findById(RecordId id) {
        return repository.findById(id.id()).map(this::toDomain);
    }

    private MedicalRecord toDomain(MedicalRecordJpaEntity entity) {
        return new MedicalRecord(
            new RecordId(entity.getId()),
            new AppointmentId(entity.getWizytaId()),
            new PatientId(entity.getPacjentId()),
            new DoctorId(entity.getLekarzId()),
            new Diagnosis(entity.getKodICD10(), entity.getDiagnozaOpis()),
            new Symptom(entity.getObjawOpis()),
            new Prescription(entity.getReceptaLek(), entity.getReceptaDawkowanie()),
            entity.getNotatki()
        );
    }
}
