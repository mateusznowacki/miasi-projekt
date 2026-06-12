import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deactivateStaff } from "@/client";

export function useDeactivateStaff() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: string) => {
      await deactivateStaff({
        path: { id },
        throwOnError: true,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["staff"] });
      queryClient.invalidateQueries({ queryKey: ["doctors"] });
    },
  });
}
