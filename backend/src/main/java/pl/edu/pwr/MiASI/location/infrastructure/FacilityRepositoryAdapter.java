package pl.edu.pwr.MiASI.location.infrastructure;

import org.springframework.stereotype.Component;
import pl.edu.pwr.MiASI.location.domain.*;
import java.util.Optional;

@Component
public class FacilityRepositoryAdapter implements FacilityRepository {
    private final SpringDataFacilityRepository repository;

    public FacilityRepositoryAdapter(SpringDataFacilityRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Facility facility) {
        FacilityJpaEntity entity = new FacilityJpaEntity(
            facility.getId().id(),
            facility.getNazwa().nazwa(),
            facility.getAdres().street(),
            facility.getAdres().city(),
            facility.getAdres().zipCode()
        );
        repository.save(entity);
    }

    @Override
    public Optional<Facility> findById(FacilityId id) {
        return repository.findById(id.id()).map(this::toDomain);
    }

    private Facility toDomain(FacilityJpaEntity entity) {
        return new Facility(
            new FacilityId(entity.getId()),
            new FacilityName(entity.getNazwa()),
            new Address(entity.getUlica(), entity.getMiasto(), entity.getKodPocztowy())
        );
    }
}
