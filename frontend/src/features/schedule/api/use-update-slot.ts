import { useMutation, useQueryClient } from "@tanstack/react-query";
import { dbUpdateSlot, type NewSlotInput } from "@/shared/api/mock-db";

export function useUpdateSlot() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ slotId, data }: { slotId: string; data: NewSlotInput }) =>
      dbUpdateSlot(slotId, data),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["schedule"] }),
  });
}
