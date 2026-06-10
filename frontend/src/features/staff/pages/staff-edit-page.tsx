import { Link, getRouteApi } from "@tanstack/react-router";
import { ArrowLeft } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { PageHeader } from "@/shared/components/page-header";
import { useStaffMember } from "../api/use-staff-member";
import { StaffForm } from "../components/staff-form";

const route = getRouteApi("/_app/staff/$id/edit");

export function StaffEditPage() {
  const { id } = route.useParams();
  const { data, isPending, isError, error } = useStaffMember(id);

  return (
    <div className="space-y-6">
      <Button asChild variant="ghost" size="sm" className="-ml-2">
        <Link to="/staff/$id" params={{ id }}>
          <ArrowLeft className="size-4" />
          Wróć do profilu
        </Link>
      </Button>
      <PageHeader title="Edycja pracownika" />

      {isPending ? (
        <Skeleton className="h-96 w-full" />
      ) : isError ? (
        <p className="text-sm text-destructive">{error.message}</p>
      ) : (
        <Card>
          <CardContent className="pt-6">
            <StaffForm initial={data} />
          </CardContent>
        </Card>
      )}
    </div>
  );
}
