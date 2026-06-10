import { useQuery } from "@tanstack/react-query";
import { dbListStaff } from "@/shared/api/mock-db";
import type { StaffRole } from "@/shared/types/staff-member";

export function useStaffList(filters: {
  role?: StaffRole;
  name?: string;
  specialization?: string;
}) {
  return useQuery({
    queryKey: ["staff", "list", filters],
    queryFn: () => dbListStaff(filters),
  });
}
