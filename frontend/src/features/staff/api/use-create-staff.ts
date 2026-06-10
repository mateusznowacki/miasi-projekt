import { useMutation, useQueryClient } from "@tanstack/react-query";
import { dbCreateStaff, type CreateStaffInput } from "@/shared/api/mock-db";

export function useCreateStaff() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (input: CreateStaffInput) => dbCreateStaff(input),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["staff"] });
      queryClient.invalidateQueries({ queryKey: ["doctors"] });
    },
  });
}
