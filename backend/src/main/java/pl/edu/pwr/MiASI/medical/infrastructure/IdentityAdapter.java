package pl.edu.pwr.MiASI.medical.infrastructure;

import org.springframework.stereotype.Component;
import pl.edu.pwr.MiASI.medical.domain.*;
import pl.edu.pwr.MiASI.iam.domain.AccountRepository;
import pl.edu.pwr.MiASI.iam.domain.AccountId;

import java.util.Optional;

@Component
public class IdentityAdapter implements IdentityPort {
    private final AccountRepository kontoRepository;

    public IdentityAdapter(AccountRepository kontoRepository) {
        this.kontoRepository = kontoRepository;
    }

    @Override
    public Optional<PatientData> getPatientData(PatientId patientId) {
        return kontoRepository.findById(new AccountId(patientId.id()))
            .map(account -> new PatientData(
                patientId, 
                account.getEmail().value(), 
                account.getPesel().value()
            ));
    }
}
