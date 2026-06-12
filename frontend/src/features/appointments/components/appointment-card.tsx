import { Link } from "@tanstack/react-router";
import { CalendarDays, ChevronRight, Clock, MapPin } from "lucide-react";
import type { VisitDto } from "@/client";
import { AppointmentStatusBadge } from "@/shared/components/status-badge";
import { formatDate, formatTime } from "@/shared/lib/format-date";
import type { AppointmentStatus } from "@/shared/types/appointment";
import type { Role } from "@/shared/types/role";

export function AppointmentCard({
  appointment,
  viewerRole,
}: {
  appointment: VisitDto;
  viewerRole: Role;
}) {
  const counterpart =
    viewerRole === "doctor" ? appointment.patientName : appointment.doctorName;
  const counterpartLabel = viewerRole === "doctor" ? "Pacjent" : "Lekarz";

  if (!appointment.id) return null;

  return (
    <Link
      to="/appointments/$id"
      params={{ id: appointment.id }}
      search={{ patientId: appointment.patientId }}
      className="group flex items-center gap-4 rounded-xl border bg-card p-4 transition-colors hover:border-primary/40 hover:bg-accent/40"
    >
      <div className="flex size-11 shrink-0 flex-col items-center justify-center rounded-lg bg-primary/10 text-primary">
        <CalendarDays className="size-5" />
      </div>
      <div className="min-w-0 flex-1">
        <div className="flex items-center gap-2">
          <p className="truncate font-medium">{counterpart}</p>
          <AppointmentStatusBadge status={appointment.status as AppointmentStatus} />
        </div>
        <p className="text-sm text-muted-foreground">
          {counterpartLabel} · {appointment.type}
        </p>
        <div className="mt-2 flex flex-wrap items-center gap-x-4 gap-y-1 text-sm text-muted-foreground">
          <span className="inline-flex items-center gap-1">
            <Clock className="size-3.5" />
            {formatDate(appointment.date ?? "")}, {formatTime(appointment.date ?? "")}
          </span>
          <span className="inline-flex items-center gap-1">
            <MapPin className="size-3.5" />
            {appointment.room}
          </span>
        </div>
      </div>
      <ChevronRight className="size-5 shrink-0 text-muted-foreground transition-transform group-hover:translate-x-0.5" />
    </Link>
  );
}
