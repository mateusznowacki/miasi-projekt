package pl.edu.pwr.MiASI.medical.domain;

import pl.edu.pwr.MiASI.shared.domain.AggregateRoot;
import pl.edu.pwr.MiASI.staff.domain.DoctorId;

@AggregateRoot
public class MedicalRecord {
    private RecordId id;
    private AppointmentId wizytaId;
    private PatientId patientId;
    private DoctorId lekarzId;
    private Diagnosis diagnosis;
    private Symptom symptom;
    private Prescription prescription;
    private String notes;

    public MedicalRecord(RecordId id, AppointmentId wizytaId, PatientId patientId, DoctorId lekarzId, Diagnosis diagnosis, Symptom symptom, Prescription prescription, String notes) {
        this.id = id;
        this.wizytaId = wizytaId;
        this.patientId = patientId;
        this.lekarzId = lekarzId;
        this.diagnosis = diagnosis;
        this.symptom = symptom;
        this.prescription = prescription;
        this.notes = notes;
    }

    public static MedicalRecord create(AppointmentId wizytaId, PatientId patientId, DoctorId lekarzId, Diagnosis diagnosis, Symptom symptom, Prescription prescription, String notes) {
        return new MedicalRecord(RecordId.generate(), wizytaId, patientId, lekarzId, diagnosis, symptom, prescription, notes);
    }

    public RecordId getId() { return id; }
    public AppointmentId getWizytaId() { return wizytaId; }
    public PatientId getPacjentId() { return patientId; }
    public DoctorId getLekarzId() { return lekarzId; }
    public Diagnosis getDiagnoza() { return diagnosis; }
    public Symptom getObjaw() { return symptom; }
    public Prescription getRecepta() { return prescription; }
    public String getNotatki() { return notes; }
}
