import { useMutation } from "@tanstack/react-query";
import { apiClient } from "@/shared/api/api-client";
import type { RegisterPatientInput } from "@/shared/api/mock-db"; // Keeping the type from mock-db for now, or we can redefine it

export function useRegister() {
  return useMutation({
    mutationFn: async (input: RegisterPatientInput) => {
      return apiClient("/patients/register", {
        method: "POST",
        body: JSON.stringify(input),
      }) as Promise<{ patientId: string }>;
    },
  });
}
