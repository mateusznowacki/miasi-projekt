import { useQuery } from "@tanstack/react-query";
import { dbGetSchedule } from "@/shared/api/mock-db";

export function useDaySchedule(doctorId: string | null, date: Date | undefined) {
  return useQuery({
    queryKey: ["schedule", "day", doctorId, date?.toDateString()],
    queryFn: () => {
      const from = new Date(date!);
      from.setHours(0, 0, 0, 0);
      const to = new Date(date!);
      to.setHours(23, 59, 59, 999);
      return dbGetSchedule(doctorId!, from.toISOString(), to.toISOString());
    },
    enabled: Boolean(doctorId && date),
  });
}
