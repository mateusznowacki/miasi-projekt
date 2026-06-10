import { useQuery } from "@tanstack/react-query";
import { dbListDoctors } from "@/shared/api/mock-db";

export function useDoctors(filters: { specialization?: string; name?: string }) {
  return useQuery({
    queryKey: ["doctors", filters],
    queryFn: () => dbListDoctors(filters),
  });
}
