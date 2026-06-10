import { useQuery } from "@tanstack/react-query";
import { dbListAppointments } from "@/shared/api/mock-db";
import type { Role } from "@/shared/types/role";

export type AppointmentFilter = "upcoming" | "past";

export function useAppointmentsList(params: {
  userId: string;
  role: Role;
  filter: AppointmentFilter;
}) {
  return useQuery({
    queryKey: ["appointments", "list", params.role, params.userId, params.filter],
    queryFn: () => dbListAppointments(params),
  });
}
