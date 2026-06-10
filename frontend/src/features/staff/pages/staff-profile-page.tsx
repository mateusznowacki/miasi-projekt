import { Link, getRouteApi } from "@tanstack/react-router";
import { ArrowLeft, Pencil } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { InfoRow } from "@/shared/components/info-row";
import { PageHeader } from "@/shared/components/page-header";
import { ROLE_LABELS } from "@/shared/types/role";
import { useAuth } from "@/shared/auth/use-auth";
import { useStaffMember } from "../api/use-staff-member";
import { DeactivateStaffDrawer } from "../components/deactivate-staff-drawer";

const route = getRouteApi("/_app/staff/$id/");

export function StaffProfilePage() {
  const auth = useAuth();
  const { id } = route.useParams();
  const { data, isPending, isError, error } = useStaffMember(id);
  const isAdmin = auth?.role === "admin";

  if (isPending) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-9 w-56" />
        <Skeleton className="h-48 w-full" />
      </div>
    );
  }

  if (isError) return <p className="text-sm text-destructive">{error.message}</p>;

  const isDoctor = data.role === "doctor";

  return (
    <div className="space-y-6">
      <Button asChild variant="ghost" size="sm" className="-ml-2">
        <Link to="/staff">
          <ArrowLeft className="size-4" />
          Wróć do personelu
        </Link>
      </Button>

      <PageHeader
        title={`${isDoctor ? "dr " : ""}${data.firstName} ${data.lastName}`}
        description={ROLE_LABELS[data.role]}
        actions={
          isAdmin ? (
            <div className="flex items-center gap-2">
              <Button asChild variant="outline">
                <Link to="/staff/$id/edit" params={{ id }}>
                  <Pencil className="size-4" />
                  Edytuj
                </Link>
              </Button>
              {data.active && <DeactivateStaffDrawer staffId={id} />}
            </div>
          ) : undefined
        }
      />

      <Card>
        <CardHeader className="flex-row items-center justify-between">
          <CardTitle>Dane pracownika</CardTitle>
          <Badge
            className={
              data.active
                ? "border-transparent bg-emerald-500/10 text-emerald-600 dark:text-emerald-400"
                : "border-transparent bg-muted text-muted-foreground"
            }
          >
            {data.active ? "Aktywny" : "Nieaktywny"}
          </Badge>
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
    </div>
  );
}
