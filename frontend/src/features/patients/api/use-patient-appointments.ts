import { useQuery } from "@tanstack/react-query";
import { dbListAppointmentsByPatient } from "@/shared/api/mock-db";

export function usePatientAppointments(patientId: string) {
  return useQuery({
    queryKey: ["appointments", "by-patient", patientId],
    queryFn: () => dbListAppointmentsByPatient(patientId),
  });
}
