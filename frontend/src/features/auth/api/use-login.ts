import { useMutation } from "@tanstack/react-query";
import { login } from "@/client";
import { setAuth } from "@/shared/auth/auth-store";
import { mapAuthResult } from "../types/map-auth-result";

export function useLogin() {
  return useMutation({
    mutationFn: async ({ email, password }: { email: string; password: string }) => {
      const { data } = await login({
        body: { email, password },
        throwOnError: true,
      });
      return data;
    },
    onSuccess: (result) => {
      setAuth(mapAuthResult(result));
    },
  });
}
