package pl.MiASI.patient.infrastructure.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
class PatientJpaEntity {
    @Id
    private UUID id;
    private String firstName;
    private String lastName;
    private String pesel;
    private String phone;
    private String email;
    private String address;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "patient_id")
    private List<MedicalRecordJpaEntity> records;
}