package pl.edu.pwr.MiASI.location.domain;

import java.util.Optional;

public interface FacilityRepository {
    void save(Facility facility);
    Optional<Facility> findById(FacilityId id);
}
