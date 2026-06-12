import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createStaff, type CreateStaffCommand } from "@/client";
import { toBackendStaffRole } from "@/shared/types/map-staff-role";

export type CreateStaffInput =
  | {
      role: "doctor";
      firstName: string;
      lastName: string;
      email: string;
      specialization: string;
      pwz: string;
      department: string;
    }
  | {
      role: "admin_staff";
      firstName: string;
      lastName: string;
      email: string;
      position: string;
    };

function toCreateCommand(input: CreateStaffInput): CreateStaffCommand {
  const base = {
    role: toBackendStaffRole(input.role),
    firstName: input.firstName,
    lastName: input.lastName,
    email: input.email,
  };

  if (input.role === "doctor") {
    return {
      ...base,
      specialization: input.specialization,
      pwz: input.pwz,
      department: input.department,
    };
  }

  return { ...base, position: input.position };
}

export function useCreateStaff() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (input: CreateStaffInput) => {
      const { data } = await createStaff({
        body: toCreateCommand(input),
        throwOnError: true,
      });
      return { staffId: data.staffId ?? "" };
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["staff"] });
      queryClient.invalidateQueries({ queryKey: ["doctors"] });
    },
  });
}
