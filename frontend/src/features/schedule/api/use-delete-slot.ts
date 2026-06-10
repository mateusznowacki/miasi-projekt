import { useMutation, useQueryClient } from "@tanstack/react-query";
import { dbDeleteSlot } from "@/shared/api/mock-db";

export function useDeleteSlot() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (slotId: string) => dbDeleteSlot(slotId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["schedule"] }),
  });
}
