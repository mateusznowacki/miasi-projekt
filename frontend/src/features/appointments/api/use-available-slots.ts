import { useQuery } from "@tanstack/react-query";
import { dbGetAvailableSlots } from "@/shared/api/mock-db";

export function useAvailableSlots(doctorId: string | null, date: string | null) {
  return useQuery({
    queryKey: ["schedule", "available", doctorId, date],
    queryFn: () => dbGetAvailableSlots(doctorId!, date!),
    enabled: Boolean(doctorId && date),
  });
}
