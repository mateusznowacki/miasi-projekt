import { useMutation, useQueryClient } from "@tanstack/react-query";
import { addTimeSlots } from "@/client";
import type { SlotInput } from "../lib/slot-helpers";

function toAddSlotCommand(slot: SlotInput) {
  return {
    timeRange: { startTime: slot.startTime, endTime: slot.endTime },
    office: slot.room,
  };
}

export function useAddSlots() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ doctorId, slots }: { doctorId: string; slots: SlotInput[] }) => {
      await addTimeSlots({
        path: { doctorId },
        body: { commands: slots.map(toAddSlotCommand) },
        throwOnError: true,
      });
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["schedule"] }),
  });
}
