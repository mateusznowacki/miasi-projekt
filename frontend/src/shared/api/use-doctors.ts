import { useQuery } from "@tanstack/react-query";
import { listDoctors } from "@/client";

export function useDoctors(filters: { specialization?: string; name?: string }) {
  const specialization = filters.specialization?.trim() || undefined;
  const name = filters.name?.trim() || undefined;

  return useQuery({
    queryKey: ["doctors", specialization, name],
    queryFn: async () => {
      const { data } = await listDoctors({
        query: { specialization, name },
        throwOnError: true,
      });
      return data.filter((doctor) => doctor.active !== false);
    },
  });
}
