import { useMutation, useQueryClient } from "@tanstack/react-query";
import { dbDeactivateStaff } from "@/shared/api/mock-db";

export function useDeactivateStaff() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => dbDeactivateStaff(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["staff"] });
      queryClient.invalidateQueries({ queryKey: ["doctors"] });
    },
  });
}
