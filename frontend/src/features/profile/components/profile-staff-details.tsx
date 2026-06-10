import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useStaffMember } from "@/features/staff/api/use-staff-member";
import { InfoRow } from "@/shared/components/info-row";
import { ROLE_LABELS } from "@/shared/types/role";

export function ProfileStaffDetails({ staffId }: { staffId: string }) {
  const { data, isPending, isError, error } = useStaffMember(staffId);

  if (isPending) return <Skeleton className="h-56 w-full" />;
  if (isError) return <p className="text-sm text-destructive">{error.message}</p>;

  const isDoctor = data.role === "doctor";

  return (
    <Card>
      <CardHeader>
        <CardTitle>Dane pracownika</CardTitle>
      </CardHeader>
      <CardContent>
        <InfoRow label="Imię i nazwisko" value={`${data.firstName} ${data.lastName}`} />
        <InfoRow label="Email" value={data.email} />
        <InfoRow label="Rola" value={ROLE_LABELS[data.role]} />
        {isDoctor ? (
          <>
            <InfoRow label="Specjalizacja" value={data.specialization ?? ""} />
            <InfoRow label="Numer PWZ" value={data.pwz ?? ""} />
            <InfoRow label="Oddział" value={data.department ?? ""} />
          </>
        ) : (
          <InfoRow label="Stanowisko" value={data.position ?? ""} />
        )}
      </CardContent>
    </Card>
  );
}
