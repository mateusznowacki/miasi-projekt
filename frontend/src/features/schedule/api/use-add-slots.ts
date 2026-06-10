import { useMutation, useQueryClient } from "@tanstack/react-query";
import { dbAddSlots, type NewSlotInput } from "@/shared/api/mock-db";

export function useAddSlots() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ doctorId, slots }: { doctorId: string; slots: NewSlotInput[] }) =>
      dbAddSlots(doctorId, slots),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["schedule"] }),
  });
}
