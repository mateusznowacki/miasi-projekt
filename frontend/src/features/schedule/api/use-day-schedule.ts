import { useQuery } from "@tanstack/react-query";
import { getScheduleByDoctor } from "@/client";

export function useDaySchedule(doctorId: string | null, date: Date | undefined) {
  return useQuery({
    queryKey: ["schedule", "day", doctorId, date?.toDateString()],
    queryFn: async () => {
      const from = new Date(date!);
      from.setHours(0, 0, 0, 0);
      const to = new Date(date!);
      to.setHours(23, 59, 59, 999);

      const { data } = await getScheduleByDoctor({
        path: { doctorId: doctorId! },
        query: { from: from.toISOString(), to: to.toISOString() },
        throwOnError: true,
      });

      return data.slots ?? [];
    },
    enabled: Boolean(doctorId && date),
  });
}
