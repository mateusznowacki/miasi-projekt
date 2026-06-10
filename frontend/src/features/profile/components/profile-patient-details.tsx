import { Link } from "@tanstack/react-router";
import { HeartPulse, UserPen } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { usePatient } from "@/features/patients/api/use-patient";
import { InfoRow } from "@/shared/components/info-row";

export function ProfilePatientDetails({ patientId }: { patientId: string }) {
  const { data, isPending, isError, error } = usePatient(patientId);

  if (isPending) return <Skeleton className="h-64 w-full" />;
  if (isError) return <p className="text-sm text-destructive">{error.message}</p>;

  const { personalData: pd, medicalData: md } = data;

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
          <InfoRow label="Imię i nazwisko" value={`${pd.firstName} ${pd.lastName}`} />
          <InfoRow label="Email" value={pd.email} />
          <InfoRow label="Telefon" value={pd.phone} />
          <InfoRow label="PESEL" value={pd.pesel} />
          <InfoRow label="Adres" value={pd.address} />
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <HeartPulse className="size-4 text-primary" />
            Dane medyczne
          </CardTitle>
        </CardHeader>
        <CardContent>
          <InfoRow label="Grupa krwi" value={md.bloodType} />
          <InfoRow label="Alergie" value={md.allergies} />
          <InfoRow label="Choroby przewlekłe" value={md.chronicDiseases} />
          <InfoRow label="Leki" value={md.medications} />
        </CardContent>
      </Card>
    </div>
  );
}
