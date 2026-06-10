import { useQuery } from "@tanstack/react-query";
import { dbListPatients } from "@/shared/api/mock-db";

export function usePatientsList(filters: { name?: string; pesel?: string }) {
  return useQuery({
    queryKey: ["patients", "list", filters],
    queryFn: () => dbListPatients(filters),
  });
}
