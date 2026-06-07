package pl.edu.pwr.MiASI.staff.domain;

import java.util.Optional;

public interface DoctorRepository {
    void save(Doctor doctor);
    Optional<Doctor> findById(DoctorId id);
}
