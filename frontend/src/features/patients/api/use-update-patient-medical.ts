import { useMutation, useQueryClient } from "@tanstack/react-query";
import { dbUpdatePatientMedical } from "@/shared/api/mock-db";
import type { PatientMedicalData } from "@/shared/types/patient";

export function useUpdatePatientMedical(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: Partial<PatientMedicalData>) => dbUpdatePatientMedical(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["patients"] });
    },
  });
}
