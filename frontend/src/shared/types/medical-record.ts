export interface MedicalRecord {
  id: string;
  appointmentId: string;
  patientId: string;
  diagnoses: string;
  symptoms: string;
  prescriptions: string;
  notes: string;
  createdAt: string;
}
