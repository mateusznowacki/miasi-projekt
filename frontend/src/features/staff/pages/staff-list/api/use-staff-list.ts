import { useQuery } from "@tanstack/react-query";
import { listStaff, searchStaff } from "@/client";
import type { StaffRole } from "@/shared/types/staff-member";
import { toBackendStaffRole } from "@/shared/types/map-staff-role";

function toSearchNameQuery(name: string) {
  const trimmed = name.trim();
  if (!trimmed) return {};
  const parts = trimmed.split(/\s+/);
  if (parts.length >= 2) {
    return { firstName: parts[0], lastName: parts.slice(1).join(" ") };
  }
  return { lastName: parts[0] };
}

export function useStaffList(filters: { role?: StaffRole; name?: string }) {
  const name = filters.name?.trim() ?? "";
  const backendRole = filters.role ? toBackendStaffRole(filters.role) : undefined;

  return useQuery({
    queryKey: ["staff", "list", { role: filters.role, name }],
    queryFn: async () => {
      if (name) {
        const { data } = await searchStaff({
          query: { ...toSearchNameQuery(name), role: backendRole },
          throwOnError: true,
        });
        return data;
      }

      const { data } = await listStaff({
        query: backendRole ? { role: backendRole } : undefined,
        throwOnError: true,
      });
      return data;
    },
  });
}
