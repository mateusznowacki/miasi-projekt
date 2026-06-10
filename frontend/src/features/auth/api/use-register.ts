import { useMutation } from "@tanstack/react-query";
import { dbRegisterPatient, type RegisterPatientInput } from "@/shared/api/mock-db";

export function useRegister() {
  return useMutation({
    mutationFn: (input: RegisterPatientInput) => dbRegisterPatient(input),
  });
}
