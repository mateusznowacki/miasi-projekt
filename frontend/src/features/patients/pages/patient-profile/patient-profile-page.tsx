import { Link, getRouteApi } from "@tanstack/react-router";
import { CalendarX2, FileText, UserPen } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { AppointmentCard } from "@/features/appointments/components/appointment-card";
import { EmptyState } from "@/shared/components/empty-state";
import { InfoRow } from "@/shared/components/info-row";
import { ListSkeleton } from "@/shared/components/list-skeleton";
import { PageHeader } from "@/shared/components/page-header";
import { useAuth } from "@/shared/auth/use-auth";
import { usePatient } from "@/shared/api/use-patient";
import { formatDate } from "@/shared/lib/format-date";
import { usePatientVisits } from "./api/use-patient-visits";

const route = getRouteApi("/_app/patients/$id/");

export function PatientProfilePage() {
  const auth = useAuth();
  const { id } = route.useParams();
  const { data, isPending, isError, error } = usePatient(id);
  const visits = usePatientVisits(id);

  if (isPending) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-9 w-56" />
        <Skeleton className="h-40 w-full" />
      </div>
    );
  }

  if (isError || !data) {
    return <p className="text-sm text-destructive">{error?.message ?? "Nie znaleziono pacjenta"}</p>;
  }

  const canEditPersonal = auth?.role === "admin_staff" || auth?.role === "patient";
  const medicalRecords = data.medicalRecords ?? [];

  return (
    <div className="space-y-6">
      <PageHeader
        title={`${data.firstName ?? ""} ${data.lastName ?? ""}`}
        description="Profil pacjenta"
      />

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <Card>
          <CardHeader className="flex-row items-center justify-between">
            <CardTitle>Dane osobowe</CardTitle>
            {canEditPersonal && (
              <Button asChild variant="outline" size="sm">
                <Link to="/patients/$id/edit" params={{ id }}>
                  <UserPen className="size-4" />
                  Edytuj
                </Link>
              </Button>
            )}
          </CardHeader>
          <CardContent>
            <InfoRow label="Imię i nazwisko" value={`${data.firstName ?? ""} ${data.lastName ?? ""}`} />
            <InfoRow label="Email" value={data.email ?? "—"} />
            <InfoRow label="Telefon" value={data.phone ?? "—"} />
            <InfoRow label="PESEL" value={data.pesel ?? "—"} />
            <InfoRow label="Adres" value={data.address ?? "—"} />
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <FileText className="size-4 text-primary" />
              Historia medyczna
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {medicalRecords.length === 0 ? (
              <p className="text-sm text-muted-foreground">Brak wpisów w historii medycznej.</p>
            ) : (
              medicalRecords.map((record) => (
                <div key={record.recordId} className="rounded-lg border p-3">
                  <p className="font-medium">{record.diagnoses ?? "—"}</p>
                  {record.createdAt && (
                    <p className="mt-1 text-xs text-muted-foreground">
                      {formatDate(record.createdAt)}
                    </p>
                  )}
                </div>
              ))
            )}
          </CardContent>
        </Card>
      </div>

      <section>
        <h2 className="mb-3 text-lg font-semibold">Historia wizyt</h2>
        {visits.isPending ? (
          <ListSkeleton count={2} />
        ) : visits.data && visits.data.length > 0 ? (
          <div className="flex flex-col gap-3">
            {visits.data.map((visit) => (
              <AppointmentCard
                key={visit.id}
                appointment={visit}
                viewerRole={auth?.role ?? "admin_staff"}
              />
            ))}
          </div>
        ) : (
          <EmptyState
            icon={CalendarX2}
            title="Brak wizyt"
            description="Pacjent nie ma jeszcze żadnych wizyt."
          />
        )}
      </section>
    </div>
  );
}
