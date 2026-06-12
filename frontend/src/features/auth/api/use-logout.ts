import { useMutation } from "@tanstack/react-query";
import { logout } from "@/client";
import { clearAuth } from "@/shared/auth/auth-store";

export function useLogout() {
  return useMutation({
    mutationFn: async () => {
      const { data } = await logout({ throwOnError: true });
      return data;
    },
    onSettled: () => {
      clearAuth();
    },
  });
}
