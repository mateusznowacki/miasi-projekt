import { useMutation, useQueryClient } from "@tanstack/react-query";
import { dbUpdatePatientPersonal } from "@/shared/api/mock-db";
import type { PatientPersonalData } from "@/shared/types/patient";

export function useUpdatePatientPersonal(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: Partial<PatientPersonalData>) => dbUpdatePatientPersonal(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["patients"] });
    },
  });
}
