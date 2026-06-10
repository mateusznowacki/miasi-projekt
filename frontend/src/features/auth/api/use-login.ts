import { useMutation } from "@tanstack/react-query";
import { dbLogin } from "@/shared/api/mock-db";
import { setAuth } from "@/shared/auth/auth-store";

export function useLogin() {
  return useMutation({
    mutationFn: ({ email, password }: { email: string; password: string }) =>
      dbLogin(email, password),
    onSuccess: (user) => {
      setAuth(user);
    },
  });
}
