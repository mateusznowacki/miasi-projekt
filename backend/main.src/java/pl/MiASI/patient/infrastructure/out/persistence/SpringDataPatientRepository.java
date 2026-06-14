package pl.MiASI.patient.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface SpringDataPatientRepository extends JpaRepository<PatientJpaEntity, UUID> {
    boolean existsByPesel(String pesel);
}