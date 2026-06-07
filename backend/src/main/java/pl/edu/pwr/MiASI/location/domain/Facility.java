package pl.edu.pwr.MiASI.location.domain;

import pl.edu.pwr.MiASI.shared.domain.AggregateRoot;
import java.util.ArrayList;
import java.util.List;

@AggregateRoot
public class Facility {
    private FacilityId id;
    private FacilityName nazwa;
    private Address address;
    private List<Department> departments = new ArrayList<>();

    public Facility(FacilityId id, FacilityName nazwa, Address address) {
        this.id = id;
        this.nazwa = nazwa;
        this.address = address;
    }

    public void addDepartment(Department department) {
        this.departments.add(department);
    }

    public FacilityId getId() { return id; }
    public FacilityName getNazwa() { return nazwa; }
    public Address getAdres() { return address; }
    public List<Department> getOddzialy() { return departments; }
}
