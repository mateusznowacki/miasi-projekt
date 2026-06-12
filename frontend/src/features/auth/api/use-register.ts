import { useMutation } from "@tanstack/react-query";
import { register } from "@/client";
import type { RegisterPatientInput } from "../types/register-patient-input";

export function useRegister() {
  return useMutation({
    mutationFn: async (input: RegisterPatientInput) => {
      const { data } = await register({
        body: input,
        throwOnError: true,
      });
      return { patientId: data.patientId ?? "" };
    },
  });
}
