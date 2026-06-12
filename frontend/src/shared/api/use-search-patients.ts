import { useQuery } from "@tanstack/react-query";
import { searchPatients, type Patient } from "@/client";

function toSearchQuery(query: string) {
  const parts = query.trim().split(/\s+/);
  if (parts.length >= 2) {
    return { firstName: parts[0], lastName: parts.slice(1).join(" ") };
  }
  return { lastName: parts[0] };
}

export function useSearchPatients(query: string) {
  const trimmed = query.trim();

  return useQuery({
    queryKey: ["patients", "search", trimmed],
    enabled: trimmed.length >= 2,
    queryFn: async () => {
      const { data } = await searchPatients({
        query: toSearchQuery(trimmed),
        throwOnError: true,
      });
      return data;
    },
  });
}

export function getPatientId(patient: Patient | { id?: { value?: string } }): string {
  return patient.id?.value ?? "";
}
