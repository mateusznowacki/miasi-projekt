import { useMutation, useQueryClient } from "@tanstack/react-query";
import { dbCancelAppointment } from "@/shared/api/mock-db";

export function useCancelAppointment() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => dbCancelAppointment(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["appointments"] });
      queryClient.invalidateQueries({ queryKey: ["schedule"] });
    },
  });
}
