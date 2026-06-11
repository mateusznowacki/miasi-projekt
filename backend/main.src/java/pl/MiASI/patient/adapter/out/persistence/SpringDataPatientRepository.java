package pl.MiASI.patient.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataPatientRepository extends JpaRepository<PatientJpaEntity, UUID> {
    boolean existsByPesel(String pesel);
}