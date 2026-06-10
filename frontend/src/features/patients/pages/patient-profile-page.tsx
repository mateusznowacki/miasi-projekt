import { Link, getRouteApi } from "@tanstack/react-router";
import { CalendarX2, HeartPulse, Pencil, UserPen } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { AppointmentCard } from "@/features/appointments/components/appointment-card";
import { EmptyState } from "@/shared/components/empty-state";
import { InfoRow } from "@/shared/components/info-row";
import { ListSkeleton } from "@/shared/components/list-skeleton";
import { PageHeader } from "@/shared/components/page-header";
import { useAuth } from "@/shared/auth/use-auth";
import { usePatient } from "../api/use-patient";
import { usePatientAppointments } from "../api/use-patient-appointments";

const route = getRouteApi("/_app/patients/$id/");

export function PatientProfilePage() {
  const auth = useAuth();
  const { id } = route.useParams();
  const { data, isPending, isError, error } = usePatient(id);
  const appointments = usePatientAppointments(id);

  if (isPending) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-9 w-56" />
        <Skeleton className="h-40 w-full" />
      </div>
    );
  }

  if (isError) return <p className="text-sm text-destructive">{error.message}</p>;

  const { personalData: pd, medicalData: md } = data;
  const canEditPersonal = auth?.role === "admin_staff" || auth?.role === "patient";
  const canEditMedical = auth?.role === "doctor" || auth?.role === "admin_staff";

  return (
    <div className="space-y-6">
      <PageHeader title={`${pd.firstName} ${pd.lastName}`} description="Profil pacjenta" />

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
            <InfoRow label="Imię i nazwisko" value={`${pd.firstName} ${pd.lastName}`} />
            <InfoRow label="Email" value={pd.email} />
            <InfoRow label="Telefon" value={pd.phone} />
            <InfoRow label="PESEL" value={pd.pesel} />
            <InfoRow label="Adres" value={pd.address} />
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex-row items-center justify-between">
            <CardTitle className="flex items-center gap-2">
              <HeartPulse className="size-4 text-primary" />
              Dane medyczne
            </CardTitle>
            {canEditMedical && (
              <Button asChild variant="outline" size="sm">
                <Link to="/patients/$id/medical" params={{ id }}>
                  <Pencil className="size-4" />
                  Edytuj
                </Link>
              </Button>
            )}
          </CardHeader>
          <CardContent>
            <InfoRow label="Grupa krwi" value={md.bloodType} />
            <InfoRow label="Alergie" value={md.allergies} />
            <InfoRow label="Choroby przewlekłe" value={md.chronicDiseases} />
            <InfoRow label="Leki" value={md.medications} />
          </CardContent>
        </Card>
      </div>

      <section>
        <h2 className="mb-3 text-lg font-semibold">Historia wizyt</h2>
        {appointments.isPending ? (
          <ListSkeleton count={2} />
        ) : appointments.data && appointments.data.length > 0 ? (
          <div className="flex flex-col gap-3">
            {appointments.data.map((appointment) => (
              <AppointmentCard
                key={appointment.id}
                appointment={appointment}
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
