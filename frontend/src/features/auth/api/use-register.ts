import { useMutation } from "@tanstack/react-query";
import { register, type RegisterReq } from "@/client";

export function useRegister() {
  return useMutation({
    mutationFn: async (input: RegisterReq) => {
      const { data } = await register({
        body: input,
        throwOnError: true,
      });
      return { patientId: data.patientId ?? "" };
    },
  });
}
