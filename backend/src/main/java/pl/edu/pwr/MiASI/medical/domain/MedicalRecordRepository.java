package pl.edu.pwr.MiASI.medical.domain;
import java.util.Optional;

public interface MedicalRecordRepository {
    void save(MedicalRecord rekord);
    Optional<MedicalRecord> findById(RecordId id);
}
