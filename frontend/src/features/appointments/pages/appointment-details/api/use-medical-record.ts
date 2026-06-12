import { useQuery } from "@tanstack/react-query";
import { getMedicalRecordByVisitId } from "@/client";

export function useMedicalRecord(patientId: string, visitId: string, enabled = true) {
  return useQuery({
    queryKey: ["medical-records", patientId, visitId],
    enabled: enabled && Boolean(patientId && visitId),
    queryFn: async () => {
      const result = await getMedicalRecordByVisitId({
        path: { patientId, visitId },
      });

      if (result.response?.status === 404) {
        return null;
      }

      if (!result.response?.ok) {
        throw new Error("Nie udało się pobrać rekordu medycznego");
      }

      return result.data;
    },
  });
}
