import { useMutation } from "@tanstack/react-query";
import { setAuth } from "@/shared/auth/auth-store";
import { apiClient } from "@/shared/api/api-client";
import type { AuthUser } from "@/shared/types/auth-user";

export function useLogin() {
  return useMutation({
    mutationFn: async ({ email, password }: { email: string; password: string }) => {
      return apiClient("/auth/login", {
        method: "POST",
        body: JSON.stringify({ email, password }),
      }) as Promise<AuthUser>;
    },
    onSuccess: (user) => {
      setAuth(user);
    },
  });
}
