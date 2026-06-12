import { useQuery } from "@tanstack/react-query";
import { format } from "date-fns";
import { getAvailableSlots, type StaffDto } from "@/client";

export function useAvailableSlots(doctor: StaffDto | null, date: Date | undefined) {
  const dateKey = date ? format(date, "yyyy-MM-dd") : null;

  return useQuery({
    queryKey: ["schedule", "available", doctor?.id, dateKey],
    enabled: Boolean(doctor?.id && doctor.lastName && dateKey),
    queryFn: async () => {
      const { data } = await getAvailableSlots({
        query: {
          from: dateKey!,
          to: dateKey!,
          doctorLastName: doctor!.lastName,
        },
        throwOnError: true,
      });
      return data.filter((slot) => slot.doctorId === doctor!.id);
    },
  });
}
