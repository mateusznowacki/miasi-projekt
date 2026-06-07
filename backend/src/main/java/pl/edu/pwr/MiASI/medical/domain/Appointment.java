package pl.edu.pwr.MiASI.medical.domain;

import pl.edu.pwr.MiASI.shared.domain.AggregateRoot;
import pl.edu.pwr.MiASI.staff.domain.DoctorId;
import java.util.List;

@AggregateRoot
public class Appointment {
    private AppointmentId id;
    private PatientId patientId;
    private DoctorId lekarzId;
    private ConsultationType typ;
    private AppointmentStatus status;
    private List<SlotId> slots;

    public Appointment(AppointmentId id, PatientId patientId, DoctorId lekarzId, ConsultationType typ, List<SlotId> slots) {
        this.id = id;
        this.patientId = patientId;
        this.lekarzId = lekarzId;
        this.typ = typ;
        this.status = AppointmentStatus.RESERVED;
        this.slots = slots;
    }

    public void cancel() {
        if (this.status != AppointmentStatus.RESERVED) {
            throw new IllegalStateException("Nie można anulować appointments o statusie: " + this.status);
        }
        this.status = AppointmentStatus.CANCELLED;
    }

    public void complete() {
        if (this.status != AppointmentStatus.RESERVED) {
            throw new IllegalStateException("Nie można zakończyć appointments o statusie: " + this.status);
        }
        this.status = AppointmentStatus.COMPLETED;
    }

    public AppointmentId getId() { return id; }
    public PatientId getPacjentId() { return patientId; }
    public DoctorId getLekarzId() { return lekarzId; }
    public ConsultationType getTyp() { return typ; }
    public AppointmentStatus getStatus() { return status; }
    public List<SlotId> getSloty() { return slots; }
}
