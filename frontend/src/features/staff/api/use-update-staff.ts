import { useMutation, useQueryClient } from "@tanstack/react-query";
import { dbUpdateStaff } from "@/shared/api/mock-db";
import type { StaffMember } from "@/shared/types/staff-member";

export function useUpdateStaff(id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: Partial<Omit<StaffMember, "id">>) => dbUpdateStaff(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["staff"] });
      queryClient.invalidateQueries({ queryKey: ["doctors"] });
    },
  });
}
