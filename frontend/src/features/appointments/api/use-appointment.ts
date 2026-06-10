import { useQuery } from "@tanstack/react-query";
import { dbGetAppointment } from "@/shared/api/mock-db";

export function useAppointment(id: string) {
  return useQuery({
    queryKey: ["appointments", "detail", id],
    queryFn: () => dbGetAppointment(id),
  });
}
