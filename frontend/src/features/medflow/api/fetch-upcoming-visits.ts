import type { UpcomingVisit } from "../types/upcoming-visit";

const MOCK_VISITS: UpcomingVisit[] = [
  {
    id: "1",
    doctorName: "dr Anna Kowalska",
    specialty: "Internista",
    date: "2026-06-15T10:30:00",
    clinic: "Medflow Centrum, Warszawa",
  },
  {
    id: "2",
    doctorName: "dr Piotr Nowak",
    specialty: "Kardiolog",
    date: "2026-06-22T14:00:00",
    clinic: "Medflow Mokotów, Warszawa",
  },
];

export async function fetchUpcomingVisits(): Promise<UpcomingVisit[]> {
  await new Promise((resolve) => setTimeout(resolve, 400));
  return MOCK_VISITS;
}
