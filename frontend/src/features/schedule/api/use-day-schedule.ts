import { useQuery } from "@tanstack/react-query";
import { format } from "date-fns";
import { getScheduleByDoctor } from "@/client";

export function useDaySchedule(doctorId: string | null, date: Date | undefined) {
  const dateKey = date ? format(date, "yyyy-MM-dd") : null;

  return useQuery({
    queryKey: ["schedule", "day", doctorId, dateKey],
    queryFn: async () => {
      const { data } = await getScheduleByDoctor({
        path: { doctorId: doctorId! },
        query: { date: dateKey! },
        throwOnError: true,
      });

      return data.slots ?? [];
    },
    enabled: Boolean(doctorId && date),
  });
}
