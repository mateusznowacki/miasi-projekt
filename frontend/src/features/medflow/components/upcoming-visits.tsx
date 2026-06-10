import { useUpcomingVisits } from "../api/use-upcoming-visits";
import { UpcomingVisitCard } from "./upcoming-visit-card";

export function UpcomingVisits() {
  const { data, isPending, isError, error } = useUpcomingVisits();

  if (isPending) {
    return <p className="text-sm text-muted-foreground">Ładowanie wizyt...</p>;
  }

  if (isError) {
    return (
      <p className="text-sm text-destructive">
        Nie udało się załadować wizyt: {error.message}
      </p>
    );
  }

  if (data.length === 0) {
    return (
      <p className="text-sm text-muted-foreground">Brak zaplanowanych wizyt.</p>
    );
  }

  return (
    <ul className="flex flex-col gap-3">
      {data.map((visit) => (
        <li key={visit.id}>
          <UpcomingVisitCard visit={visit} />
        </li>
      ))}
    </ul>
  );
}
