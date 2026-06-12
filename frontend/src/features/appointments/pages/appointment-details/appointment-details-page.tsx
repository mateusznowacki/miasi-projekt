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
import { useAppointment } from "./api/use-appointment";
import { CancelAppointmentDrawer } from "./components/cancel-appointment-drawer";
import { ConductVisitDrawer } from "./components/conduct-visit-drawer";
import { DetailRow } from "./components/detail-row";
import { MedicalRecordView } from "./components/medical-record-view";

const route = getRouteApi("/_app/appointments/$id");

export function AppointmentDetailsPage() {
  const auth = useAuth();
  const { id } = route.useParams();
  const { data, isPending, isError, error } = useAppointment(id);

  if (isPending) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-48" />
        <Skeleton className="h-48 w-full" />
      </div>
    );
  }

  if (isError) {
    return <p className="text-sm text-destructive">{error.message}</p>;
  }

  const isBooked = data.status === "Zarezerwowana";
  const isCompleted = data.status === "Zakończona";
  const canCancel = isBooked && (auth?.role === "patient" || auth?.role === "admin_staff");
  const canConduct = isBooked && auth?.role === "doctor";

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
            {canCancel && <CancelAppointmentDrawer appointmentId={data.id} />}
            {canConduct && <ConductVisitDrawer appointmentId={data.id} />}
          </div>
        }
      />

      <Card>
        <CardHeader className="flex-row items-center justify-between">
          <CardTitle>{data.type}</CardTitle>
          <AppointmentStatusBadge status={data.status} />
        </CardHeader>
        <CardContent className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <DetailRow icon={CalendarDays} label="Data" value={formatDate(data.date)} />
          <DetailRow icon={Clock} label="Godzina" value={formatTime(data.date)} />
          <DetailRow icon={Stethoscope} label="Lekarz" value={data.doctorName} />
          <DetailRow icon={User} label="Pacjent" value={data.patientName} />
          <DetailRow icon={MapPin} label="Gabinet" value={data.room} />
        </CardContent>
      </Card>

      {isCompleted && (
        <Card>
          <CardHeader>
            <CardTitle>Rekord medyczny</CardTitle>
          </CardHeader>
          <CardContent>
            <Separator className="mb-4" />
            <MedicalRecordView appointmentId={data.id} />
          </CardContent>
        </Card>
      )}
    </div>
  );
}
