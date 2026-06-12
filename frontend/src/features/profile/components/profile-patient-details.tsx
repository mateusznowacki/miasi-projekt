import { Link } from "@tanstack/react-router";
import { FileText, UserPen } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { usePatient } from "@/shared/api/use-patient";
import { InfoRow } from "@/shared/components/info-row";
import { formatDate } from "@/shared/lib/format-date";

export function ProfilePatientDetails({ patientId }: { patientId: string }) {
  const { data, isPending, isError, error } = usePatient(patientId);

  if (isPending) return <Skeleton className="h-64 w-full" />;
  if (isError || !data) {
    return <p className="text-sm text-destructive">{error?.message ?? "Nie znaleziono pacjenta"}</p>;
  }

  const medicalRecords = data.medicalRecords ?? [];

  return (
    <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
      <Card>
        <CardHeader className="flex-row items-center justify-between">
          <CardTitle>Dane osobowe</CardTitle>
          <Button asChild variant="outline" size="sm">
            <Link to="/patients/$id/edit" params={{ id: patientId }}>
              <UserPen className="size-4" />
              Edytuj
            </Link>
          </Button>
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
                  <p className="mt-1 text-xs text-muted-foreground">{formatDate(record.createdAt)}</p>
                )}
              </div>
            ))
          )}
        </CardContent>
      </Card>
    </div>
  );
}
