import { useMutation, useQueryClient } from "@tanstack/react-query";
import { cancelVisit } from "@/client";

export function useCancelAppointment() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (visitId: string) => {
      await cancelVisit({
        path: { visitId },
        throwOnError: true,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["visits"] });
      queryClient.invalidateQueries({ queryKey: ["schedule"] });
    },
  });
}
