import { Link, getRouteApi } from "@tanstack/react-router";
import { ArrowLeft, CalendarDays, Clock, MapPin, Stethoscope, User } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";
import { PageHeader } from "@/shared/components/page-header";
import { AppointmentStatusBadge } from "@/shared/components/status-badge";
import { useAuth } from "@/shared/auth/use-auth";
import { formatDate, formatTime } from "@/shared/lib/format-date";
import type { AppointmentStatus } from "@/shared/types/appointment";
import { useAppointment } from "./api/use-appointment";
import { CancelAppointmentDrawer } from "./components/cancel-appointment-drawer";
import { ConductVisitDrawer } from "./components/conduct-visit-drawer";
import { DetailRow } from "./components/detail-row";
import { MedicalRecordView } from "./components/medical-record-view";

const CONSULTATION_LABELS: Record<string, string> = {
  GENERAL: "Konsultacja",
  FOLLOW_UP: "Kontrola",
  SPECIALIST: "Badanie",
};

const route = getRouteApi("/_app/appointments/$id");

export function AppointmentDetailsPage() {
  const auth = useAuth();
  const { id } = route.useParams();
  const { patientId: patientIdFromSearch } = route.useSearch();
  const { data, isPending, isError, error } = useAppointment(id, {
    patientId: patientIdFromSearch,
  });

  if (isPending) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-48" />
        <Skeleton className="h-48 w-full" />
      </div>
    );
  }

  if (isError || !data?.id) {
    return <p className="text-sm text-destructive">{error?.message ?? "Nie znaleziono wizyty"}</p>;
  }

  const visitId = data.id;
  const patientId = data.patientId ?? patientIdFromSearch ?? "";
  const status = data.status as AppointmentStatus;
  const isBooked = status === "Zarezerwowana";
  const isCompleted = status === "Zakończona";
  const canCancel = isBooked && (auth?.role === "patient" || auth?.role === "admin_staff");
  const canConduct = isBooked && auth?.role === "doctor" && Boolean(data.doctorId);
  const consultationLabel = CONSULTATION_LABELS[data.type ?? ""] ?? data.type ?? "—";

  return (
    <div className="space-y-6">
      <Button asChild variant="ghost" size="sm" className="-ml-2">
        <Link to="/appointments">
          <ArrowLeft className="size-4" />
          Wróć do wizyt
        </Link>
      </Button>

      <PageHeader
        title="Szczegóły wizyty"
        actions={
          <div className="flex items-center gap-2">
            {canCancel && <CancelAppointmentDrawer appointmentId={visitId} />}
            {canConduct && data.doctorId && (
              <ConductVisitDrawer
                patientId={patientId}
                visitId={visitId}
                doctorId={data.doctorId}
              />
            )}
          </div>
        }
      />

      <Card>
        <CardHeader className="flex-row items-center justify-between">
          <CardTitle>{consultationLabel}</CardTitle>
          <AppointmentStatusBadge status={status} />
        </CardHeader>
        <CardContent className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <DetailRow icon={CalendarDays} label="Data" value={formatDate(data.date ?? "")} />
          <DetailRow icon={Clock} label="Godzina" value={formatTime(data.date ?? "")} />
          <DetailRow icon={Stethoscope} label="Lekarz" value={data.doctorName ?? "—"} />
          <DetailRow icon={User} label="Pacjent" value={data.patientName ?? "—"} />
          <DetailRow icon={MapPin} label="Gabinet" value={data.room ?? "—"} />
        </CardContent>
      </Card>

      {isCompleted && patientId && (
        <Card>
          <CardHeader>
            <CardTitle>Rekord medyczny</CardTitle>
          </CardHeader>
          <CardContent>
            <Separator className="mb-4" />
            <MedicalRecordView patientId={patientId} visitId={visitId} />
          </CardContent>
        </Card>
      )}
    </div>
  );
}
