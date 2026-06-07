package pl.edu.pwr.MiASI.staff.domain;

import pl.edu.pwr.MiASI.shared.domain.AggregateRoot;
import pl.edu.pwr.MiASI.location.domain.FacilityId;
import pl.edu.pwr.MiASI.location.domain.DepartmentId;
import pl.edu.pwr.MiASI.location.domain.RoomId;

@AggregateRoot
public class Doctor {
    private DoctorId id;
    private FullName fullName;
    private Specialization specialization;
    private FacilityId placowkaId;
    private DepartmentId departmentId;
    private RoomId roomId;

    public Doctor(DoctorId id, FullName fullName, Specialization specialization, FacilityId placowkaId, DepartmentId departmentId, RoomId roomId) {
        this.id = id;
        this.fullName = fullName;
        this.specialization = specialization;
        this.placowkaId = placowkaId;
        this.departmentId = departmentId;
        this.roomId = roomId;
    }

    public DoctorId getId() { return id; }
    public FullName getImieNazwisko() { return fullName; }
    public Specialization getSpecjalizacja() { return specialization; }
    public FacilityId getPlacowkaId() { return placowkaId; }
    public DepartmentId getOddzialId() { return departmentId; }
    public RoomId getGabinetId() { return roomId; }
}
