import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
  dbCreateAppointment,
  type CreateAppointmentInput,
} from "@/shared/api/mock-db";

export function useCreateAppointment() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (input: CreateAppointmentInput) => dbCreateAppointment(input),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["appointments"] });
      queryClient.invalidateQueries({ queryKey: ["schedule"] });
    },
  });
}
