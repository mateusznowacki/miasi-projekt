package pl.edu.pwr.MiASI.medical.domain;

import pl.edu.pwr.MiASI.shared.domain.DomainEvent;
import pl.edu.pwr.MiASI.staff.domain.DoctorId;
import java.util.List;

public record AppointmentBooked(AppointmentId wizytaId, PatientId patientId, DoctorId lekarzId, List<SlotId> slots) implements DomainEvent {}
