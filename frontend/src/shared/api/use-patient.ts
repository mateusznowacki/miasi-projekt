import { useQuery } from "@tanstack/react-query";
import { getPatientProfile } from "@/client";

export function usePatient(patientId: string) {
  return useQuery({
    queryKey: ["patients", "detail", patientId],
    enabled: Boolean(patientId),
    queryFn: async () => {
      const { data } = await getPatientProfile({
        path: { patientId },
        throwOnError: true,
      });
      return data;
    },
  });
}
