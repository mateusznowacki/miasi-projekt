import { useMutation, useQueryClient } from "@tanstack/react-query";
import { addMedicalRecord } from "@/client";
import type { MedicalRecordFormValues } from "@/shared/components/medical-record-form";

export function useCreateMedicalRecord() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (
      input: MedicalRecordFormValues & {
        patientId: string;
        visitId: string;
        doctorId: string;
      },
    ) => {
      const { patientId, visitId, doctorId, diagnoses, symptoms, prescriptions, notes } = input;

      await addMedicalRecord({
        path: { patientId },
        body: {
          visitId,
          doctorId,
          diagnoses,
          symptoms,
          prescriptions,
          notes,
        },
        throwOnError: true,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["visits"] });
      queryClient.invalidateQueries({ queryKey: ["medical-records"] });
    },
  });
}
