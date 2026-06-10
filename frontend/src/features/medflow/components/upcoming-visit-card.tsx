import type { UpcomingVisit } from "../types/upcoming-visit";

interface UpcomingVisitCardProps {
  visit: UpcomingVisit;
}

export function UpcomingVisitCard({ visit }: UpcomingVisitCardProps) {
  const formattedDate = new Date(visit.date).toLocaleString("pl-PL", {
    weekday: "short",
    day: "numeric",
    month: "short",
    hour: "2-digit",
    minute: "2-digit",
  });

  return (
    <article className="rounded-lg border bg-card p-4 text-card-foreground">
      <p className="font-medium">{visit.doctorName}</p>
      <p className="text-sm text-muted-foreground">{visit.specialty}</p>
      <p className="mt-2 text-sm">{formattedDate}</p>
      <p className="text-sm text-muted-foreground">{visit.clinic}</p>
    </article>
  );
}
