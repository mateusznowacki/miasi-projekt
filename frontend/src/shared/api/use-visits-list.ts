import { useQuery } from "@tanstack/react-query";
import { fetchVisits, sortVisits, type VisitFilter } from "@/shared/api/fetch-visits";
import type { Role } from "@/shared/types/role";

export function useVisitsList(params: {
  userId: string;
  role: Role;
  filter: VisitFilter;
  limit?: number;
}) {
  const enabled = params.role === "patient" || params.role === "doctor";

  return useQuery({
    queryKey: ["visits", "list", params.role, params.userId, params.filter, params.limit],
    enabled,
    queryFn: () => fetchVisits(params),
    select: (visits) => {
      const sorted = sortVisits(visits, params.filter);
      return params.limit ? sorted.slice(0, params.limit) : sorted;
    },
  });
}
