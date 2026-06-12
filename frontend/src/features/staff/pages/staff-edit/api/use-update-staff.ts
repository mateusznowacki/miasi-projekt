import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateStaff, type UpdateStaffCommand } from "@/client";
import type { CreateStaffInput } from "../../staff-create/api/use-create-staff";

function toUpdateCommand(input: CreateStaffInput): UpdateStaffCommand {
  if (input.role === "doctor") {
    return {
      firstName: input.firstName,
      lastName: input.lastName,
      email: input.email,
      specialization: input.specialization,
      pwz: input.pwz,
      department: input.department,
    };
  }

  return {
    firstName: input.firstName,
    lastName: input.lastName,
    email: input.email,
    position: input.position,
  };
}

export function useUpdateStaff(id: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (input: CreateStaffInput) => {
      await updateStaff({
        path: { id },
        body: toUpdateCommand(input),
        throwOnError: true,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["staff"] });
      queryClient.invalidateQueries({ queryKey: ["doctors"] });
    },
  });
}
