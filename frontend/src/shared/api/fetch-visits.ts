import { getVisitsByDoctor, getVisitsByPatient, type VisitDto } from "@/client";
import type { Role } from "@/shared/types/role";

export type VisitFilter = "upcoming" | "past";

export async function fetchVisits(params: {
  userId: string;
  role: Role;
  filter: VisitFilter;
}): Promise<VisitDto[]> {
  const query = { filter: params.filter };

  if (params.role === "patient") {
    const { data } = await getVisitsByPatient({
      path: { patientId: params.userId },
      query,
      throwOnError: true,
    });
    return data;
  }

  if (params.role === "doctor") {
    const { data } = await getVisitsByDoctor({
      path: { doctorId: params.userId },
      query,
      throwOnError: true,
    });
    return data;
  }

  return [];
}

export function sortVisits(visits: VisitDto[], filter: VisitFilter): VisitDto[] {
  return visits.slice().sort((a, b) => {
    const cmp = (a.date ?? "").localeCompare(b.date ?? "");
    return filter === "past" ? -cmp : cmp;
  });
}
