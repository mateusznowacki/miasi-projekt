import { useQuery } from "@tanstack/react-query";
import { dbGetMedicalRecordByAppointment } from "@/shared/api/mock-db";

export function useMedicalRecord(appointmentId: string, enabled = true) {
  return useQuery({
    queryKey: ["medical-records", "by-appointment", appointmentId],
    queryFn: () => dbGetMedicalRecordByAppointment(appointmentId),
    enabled,
  });
}
