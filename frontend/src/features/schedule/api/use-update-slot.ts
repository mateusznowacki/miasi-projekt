import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateSlot } from "@/client";
import type { SlotInput } from "../lib/slot-helpers";

export function useUpdateSlot() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      doctorId,
      slotId,
      data,
    }: {
      doctorId: string;
      slotId: string;
      data: SlotInput;
    }) => {
      await updateSlot({
        path: { doctorId, slotId },
        body: {
          timeRange: { startTime: data.startTime, endTime: data.endTime },
          office: data.room,
        },
        throwOnError: true,
      });
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["schedule"] }),
  });
}
