package pl.MiASI.patient.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.MiASI.patient.domain.model.MedicalRecord;
import pl.MiASI.patient.domain.model.Patient;
import pl.MiASI.patient.domain.repository.PatientRepository;
import pl.MiASI.shared.domain.model.DoctorId;
import pl.MiASI.shared.domain.model.PatientId;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PatientRepositoryAdapter implements PatientRepository {
    private final SpringDataPatientRepository repo;

    @Override
    public void save(Patient patient) {
        PatientJpaEntity e = repo.findById(patient.getId().value()).orElse(new PatientJpaEntity());
        e.setId(patient.getId().value());
        e.setFirstName(patient.getFirstName());
        e.setLastName(patient.getLastName());
        e.setPesel(patient.getPesel());
        e.setPhone(patient.getPhone());
        e.setEmail(patient.getEmail());
        e.setAddress(patient.getAddress());

        List<MedicalRecordJpaEntity> recs = patient.getMedicalRecords().stream().map(r -> {
            MedicalRecordJpaEntity re = new MedicalRecordJpaEntity();
            re.setId(r.getRecordId());
            re.setVisitId(r.getVisitId());
            re.setDoctorId(r.getDoctorId().value());
            re.setDiagnoses(r.getDiagnoses());
            re.setSymptoms(r.getSymptoms());
            re.setPrescriptions(r.getPrescriptions());
            re.setNotes(r.getNotes());
            re.setTestResults(r.getTestResults());
            re.setCreatedAt(r.getCreatedAt());
            re.setUpdatedAt(r.getUpdatedAt());
            re.setUpdatedBy(r.getUpdatedBy() != null ? r.getUpdatedBy().value() : null);
            return re;
        }).collect(Collectors.toList());
        if (e.getRecords() == null) {
            e.setRecords(recs);
        } else {
            e.getRecords().clear();
            e.getRecords().addAll(recs);
        }
        repo.save(e);
    }

    @Override
    public Optional<Patient> findById(PatientId id) {
        return repo.findById(id.value()).map(this::mapToDomain);
    }

    @Override
    public List<Patient> findAll() {
        return repo.findAll().stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByPesel(String pesel) {
        return repo.existsByPesel(pesel);
    }

    private Patient mapToDomain(PatientJpaEntity e) {
        List<MedicalRecord> recs = e.getRecords().stream().map(re -> new MedicalRecord(
                re.getId(), re.getVisitId(), new DoctorId(re.getDoctorId()), re.getDiagnoses(), re.getSymptoms(), re.getPrescriptions(), re.getNotes(), re.getTestResults(), re.getCreatedAt(), re.getUpdatedAt(), re.getUpdatedBy() != null ? new DoctorId(re.getUpdatedBy()) : null
        )).collect(Collectors.toList());
        return new Patient(new PatientId(e.getId()), e.getFirstName(), e.getLastName(), e.getPesel(), e.getPhone(), e.getEmail(), e.getAddress(), recs);
    }
}