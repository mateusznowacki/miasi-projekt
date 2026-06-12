import { getVisitsByDoctor, getVisitsByPatient, type VisitDto } from "@/client";
import { getAuth } from "@/shared/auth/auth-store";
import type { Role } from "@/shared/types/role";

export async function fetchVisitById(
  visitId: string,
  options?: { patientId?: string; userId?: string; role?: Role },
): Promise<VisitDto> {
  const auth =
    options?.userId && options?.role
      ? { userId: options.userId, role: options.role }
      : getAuth();

  if (!auth) {
    throw new Error("Brak autoryzacji");
  }

  let visits: VisitDto[];

  if (auth.role === "patient") {
    const { data } = await getVisitsByPatient({
      path: { patientId: auth.userId },
      throwOnError: true,
    });
    visits = data;
  } else if (auth.role === "doctor") {
    const { data } = await getVisitsByDoctor({
      path: { doctorId: auth.userId },
      throwOnError: true,
    });
    visits = data;
  } else if (options?.patientId) {
    const { data } = await getVisitsByPatient({
      path: { patientId: options.patientId },
      throwOnError: true,
    });
    visits = data;
  } else {
    throw new Error("Nie znaleziono wizyty");
  }

  const visit = visits.find((entry) => entry.id === visitId);
  if (!visit) {
    throw new Error("Nie znaleziono wizyty");
  }

  return visit;
}
