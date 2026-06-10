import { Link } from "@tanstack/react-router";
import { CalendarX2 } from "lucide-react";
import { AppointmentCard } from "@/features/appointments/components/appointment-card";
import { useAppointmentsList } from "@/features/appointments/api/use-appointments-list";
import { Button } from "@/components/ui/button";
import { EmptyState } from "@/shared/components/empty-state";
import { ListSkeleton } from "@/shared/components/list-skeleton";
import type { AuthUser } from "@/shared/types/auth-user";

export function DashboardUpcoming({ user }: { user: AuthUser }) {
  const { data, isPending, isError, error } = useAppointmentsList({
    userId: user.userId,
    role: user.role,
    filter: "upcoming",
  });

  if (isPending) return <ListSkeleton count={2} />;

  if (isError) {
    return <p className="text-sm text-destructive">{error.message}</p>;
  }

  if (data.length === 0) {
    return (
      <EmptyState
        icon={CalendarX2}
        title="Brak nadchodzących wizyt"
        description="Nie masz zaplanowanych wizyt."
        action={
          user.role === "doctor" ? undefined : (
            <Button asChild>
              <Link to="/appointments/new">Umów wizytę</Link>
            </Button>
          )
        }
      />
    );
  }

  return (
    <div className="flex flex-col gap-3">
      {data.slice(0, 3).map((appointment) => (
        <AppointmentCard
          key={appointment.id}
          appointment={appointment}
          viewerRole={user.role}
        />
      ))}
    </div>
  );
}
