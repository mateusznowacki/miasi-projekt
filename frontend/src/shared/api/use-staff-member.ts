import { useQuery } from "@tanstack/react-query";
import { getStaff } from "@/client";

export function useStaffMember(id: string) {
  return useQuery({
    queryKey: ["staff", "detail", id],
    enabled: Boolean(id),
    queryFn: async () => {
      const { data } = await getStaff({
        path: { id },
        throwOnError: true,
      });
      return data;
    },
  });
}
