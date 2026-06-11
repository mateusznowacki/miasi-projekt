package pl.MiASI.medicalcare.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataVisitRepository extends JpaRepository<VisitJpaEntity, UUID> {
    List<VisitJpaEntity> findByPatientId(UUID patientId);

    List<VisitJpaEntity> findByDoctorId(UUID doctorId);
}
