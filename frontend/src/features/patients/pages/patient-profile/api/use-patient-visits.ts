import { useQuery } from "@tanstack/react-query";
import { getVisitsByPatient } from "@/client";
import { sortVisits } from "@/shared/api/fetch-visits";

export function usePatientVisits(patientId: string) {
  return useQuery({
    queryKey: ["visits", "patient", patientId],
    enabled: Boolean(patientId),
    queryFn: async () => {
      const { data } = await getVisitsByPatient({
        path: { patientId },
        throwOnError: true,
      });
      return sortVisits(data, "past");
    },
  });
}
