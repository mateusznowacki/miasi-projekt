export type AppointmentStatus = "Zarezerwowana" | "Zakończona" | "Anulowana";

export interface Appointment {
  id: string;
  date: string;
  doctorId: string;
  doctorName: string;
  patientId: string;
  patientName: string;
  status: AppointmentStatus;
  type: string;
  room: string;
}
