import { useMutation, useQueryClient } from "@tanstack/react-query";
import { removeSlot } from "@/client";

export function useDeleteSlot() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ doctorId, slotId }: { doctorId: string; slotId: string }) => {
      await removeSlot({
        path: { doctorId, slotId },
        throwOnError: true,
      });
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["schedule"] }),
  });
}
