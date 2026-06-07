package pl.edu.pwr.MiASI.location.domain;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private DepartmentId id;
    private String nazwa;
    private List<Room> rooms = new ArrayList<>();

    public Department(DepartmentId id, String nazwa) {
        this.id = id;
        this.nazwa = nazwa;
    }

    public void addRoom(Room room) {
        this.rooms.add(room);
    }

    public DepartmentId getId() { return id; }
    public String getNazwa() { return nazwa; }
    public List<Room> getGabinety() { return rooms; }
}
