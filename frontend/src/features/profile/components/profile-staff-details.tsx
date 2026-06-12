import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useStaffMember } from "@/shared/api/use-staff-member";
import { InfoRow } from "@/shared/components/info-row";
import { getStaffRoleLabel, mapStaffRole } from "@/shared/types/map-staff-role";

export function ProfileStaffDetails({ staffId }: { staffId: string }) {
  const { data, isPending, isError, error } = useStaffMember(staffId);

  if (isPending) return <Skeleton className="h-56 w-full" />;
  if (isError || !data) {
    return <p className="text-sm text-destructive">{error?.message ?? "Nie znaleziono pracownika"}</p>;
  }

  const isDoctor = mapStaffRole(data.role) === "doctor";

  return (
    <Card>
      <CardHeader>
        <CardTitle>Dane pracownika</CardTitle>
      </CardHeader>
      <CardContent>
        <InfoRow label="Imię i nazwisko" value={`${data.firstName ?? ""} ${data.lastName ?? ""}`} />
        <InfoRow label="Email" value={data.email ?? "—"} />
        <InfoRow label="Rola" value={getStaffRoleLabel(data.role)} />
        {isDoctor ? (
          <>
            <InfoRow label="Specjalizacja" value={data.specialization ?? "—"} />
            <InfoRow label="Numer PWZ" value={data.pwz ?? "—"} />
            <InfoRow label="Oddział" value={data.department ?? "—"} />
          </>
        ) : (
          <InfoRow label="Stanowisko" value={data.position ?? "—"} />
        )}
      </CardContent>
    </Card>
  );
}
