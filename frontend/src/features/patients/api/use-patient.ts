import { useQuery } from "@tanstack/react-query";
import { dbGetPatient } from "@/shared/api/mock-db";

export function usePatient(id: string) {
  return useQuery({
    queryKey: ["patients", "detail", id],
    queryFn: () => dbGetPatient(id),
  });
}
