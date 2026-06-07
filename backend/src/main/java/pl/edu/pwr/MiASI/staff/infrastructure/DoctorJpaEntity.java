package pl.edu.pwr.MiASI.staff.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "doctors", schema = "staff")
public class DoctorJpaEntity {
    @Id
    private UUID id;
    private String imie;
    private String nazwisko;
    private String specialization;
    private UUID placowkaId;
    private UUID departmentId;
    private UUID roomId;

    protected DoctorJpaEntity() {}

    public DoctorJpaEntity(UUID id, String imie, String nazwisko, String specialization, UUID placowkaId, UUID departmentId, UUID roomId) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.specialization = specialization;
        this.placowkaId = placowkaId;
        this.departmentId = departmentId;
        this.roomId = roomId;
    }

    public UUID getId() { return id; }
    public String getImie() { return imie; }
    public String getNazwisko() { return nazwisko; }
    public String getSpecjalizacja() { return specialization; }
    public UUID getPlacowkaId() { return placowkaId; }
    public UUID getOddzialId() { return departmentId; }
    public UUID getGabinetId() { return roomId; }
}
