package pl.MiASI.medicalcare.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pl.MiASI.shared.domain.model.DoctorId;
import pl.MiASI.shared.domain.model.PatientId;
import pl.MiASI.medicalcare.domain.model.SlotId;
import pl.MiASI.medicalcare.domain.model.Visit;
import pl.MiASI.medicalcare.domain.model.VisitId;
import pl.MiASI.medicalcare.domain.repository.VisitRepository;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class VisitRepositoryAdapter implements VisitRepository {

    private final SpringDataVisitRepository repository;

    @Override
    public void save(Visit visit) {
        VisitJpaEntity entity = new VisitJpaEntity();
        entity.setId(visit.getVisitId().value());
        entity.setPatientId(visit.getPatientId().value());
        entity.setDoctorId(visit.getDoctorId().value());
        entity.setConsultationType(visit.getConsultationType());
        entity.setStatus(visit.getStatus());
        entity.setSlotIds(visit.getSlotIds().stream().map(SlotId::value).collect(Collectors.toList()));
        repository.save(entity);
    }

    @Override
    public Optional<Visit> findById(VisitId visitId) {
        return repository.findById(visitId.value()).map(this::mapToDomain);
    }

    @Override
    public List<Visit> findByPatientId(PatientId patientId) {
        return repository.findByPatientId(patientId.value()).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Visit> findByDoctorId(DoctorId doctorId) {
        return repository.findByDoctorId(doctorId.value()).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    private Visit mapToDomain(VisitJpaEntity entity) {
        return new Visit(
                new VisitId(entity.getId()),
                new PatientId(entity.getPatientId()),
                new DoctorId(entity.getDoctorId()),
                entity.getConsultationType(),
                entity.getStatus(),
                entity.getSlotIds().stream().map(SlotId::new).collect(Collectors.toList())
        );
    }
}