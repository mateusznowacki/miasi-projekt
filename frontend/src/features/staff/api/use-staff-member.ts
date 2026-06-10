import { useQuery } from "@tanstack/react-query";
import { dbGetStaff } from "@/shared/api/mock-db";

export function useStaffMember(id: string) {
  return useQuery({
    queryKey: ["staff", "detail", id],
    queryFn: () => dbGetStaff(id),
  });
}
