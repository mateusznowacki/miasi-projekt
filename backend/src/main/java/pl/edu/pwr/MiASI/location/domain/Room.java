package pl.edu.pwr.MiASI.location.domain;

public class Room {
    private RoomId id;
    private Location location;

    public Room(RoomId id, Location location) {
        this.id = id;
        this.location = location;
    }

    public RoomId getId() { return id; }
    public Location getLokalizacja() { return location; }
}
