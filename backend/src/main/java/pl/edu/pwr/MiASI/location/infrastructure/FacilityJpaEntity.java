package pl.edu.pwr.MiASI.location.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "facilities", schema = "location")
public class FacilityJpaEntity {
    @Id
    private UUID id;
    private String nazwa;
    private String street;
    private String city;
    private String zipCode;

    protected FacilityJpaEntity() {}

    public FacilityJpaEntity(UUID id, String nazwa, String street, String city, String zipCode) {
        this.id = id;
        this.nazwa = nazwa;
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
    }

    public UUID getId() { return id; }
    public String getNazwa() { return nazwa; }
    public String getUlica() { return street; }
    public String getMiasto() { return city; }
    public String getKodPocztowy() { return zipCode; }
}
