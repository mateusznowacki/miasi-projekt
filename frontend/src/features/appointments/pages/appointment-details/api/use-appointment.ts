import { useQuery } from "@tanstack/react-query";
import { fetchVisitById } from "@/shared/api/fetch-visit-by-id";
import { useAuth } from "@/shared/auth/use-auth";

export function useAppointment(visitId: string, options?: { patientId?: string }) {
  const auth = useAuth();

  return useQuery({
    queryKey: ["visits", "detail", visitId, auth?.role, auth?.userId, options?.patientId],
    enabled: Boolean(auth && visitId),
    queryFn: () =>
      fetchVisitById(visitId, {
        patientId: options?.patientId,
        userId: auth!.userId,
        role: auth!.role,
      }),
  });
}
