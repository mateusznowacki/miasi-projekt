import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
  dbCreateMedicalRecord,
  type CreateMedicalRecordInput,
} from "@/shared/api/mock-db";

export function useCreateMedicalRecord() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (input: CreateMedicalRecordInput) => dbCreateMedicalRecord(input),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["appointments"] });
      queryClient.invalidateQueries({ queryKey: ["medical-records"] });
    },
  });
}
