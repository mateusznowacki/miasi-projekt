package pl.MiASI.medicalcare.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.MiASI.medicalcare.application.port.in.VisitQueryUseCase;
import pl.MiASI.medicalcare.domain.model.Visit;
import pl.MiASI.medicalcare.domain.repository.VisitRepository;
import pl.MiASI.shared.domain.model.PatientId;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisitQueryService implements VisitQueryUseCase {
    private final VisitRepository visitRepository;

    @Override
    public List<Visit> getVisitsByPatient(PatientId patientId) {
        return visitRepository.findByPatientId(patientId);
    }

    @Override
    public List<Visit> getVisitsByDoctor(pl.MiASI.shared.domain.model.DoctorId doctorId) {
        return visitRepository.findByDoctorId(doctorId);
    }
}