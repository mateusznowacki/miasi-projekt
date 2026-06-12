import { useMutation, useQueryClient } from "@tanstack/react-query";
import { reserveVisit, type ReserveVisitRequest } from "@/client";

export function useCreateAppointment() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (input: ReserveVisitRequest) => {
      const { data } = await reserveVisit({
        body: input,
        throwOnError: true,
      });
      return { appointmentId: data };
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["visits"] });
      queryClient.invalidateQueries({ queryKey: ["schedule"] });
    },
  });
}
