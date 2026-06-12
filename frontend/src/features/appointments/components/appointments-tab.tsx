import { CalendarX2 } from "lucide-react";
import { EmptyState } from "@/shared/components/empty-state";
import { ListSkeleton } from "@/shared/components/list-skeleton";
import type { AuthUser } from "@/shared/types/auth-user";
import type { VisitFilter } from "@/shared/api/fetch-visits";
import { useVisitsList } from "@/shared/api/use-visits-list";
import { AppointmentCard } from "./appointment-card";

export function AppointmentsTab({
  user,
  filter,
}: {
  user: AuthUser;
  filter: VisitFilter;
}) {
  const { data, isPending, isError, error } = useVisitsList({
    userId: user.userId,
    role: user.role,
    filter,
  });

  if (isPending) return <ListSkeleton count={3} />;
  if (isError) return <p className="text-sm text-destructive">{error.message}</p>;

  if (!data || data.length === 0) {
    return (
      <EmptyState
        icon={CalendarX2}
        title={filter === "upcoming" ? "Brak nadchodzących wizyt" : "Brak przeszłych wizyt"}
        description={
          filter === "upcoming"
            ? "Nie masz zaplanowanych wizyt."
            : "Historia wizyt jest pusta."
        }
      />
    );
  }

  return (
    <div className="flex flex-col gap-3">
      {data.map((appointment) => (
        <AppointmentCard
          key={appointment.id}
          appointment={appointment}
          viewerRole={user.role}
        />
      ))}
    </div>
  );
}
