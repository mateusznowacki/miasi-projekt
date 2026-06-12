import { Link, getRouteApi } from "@tanstack/react-router";
import { ArrowLeft } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { PageHeader } from "@/shared/components/page-header";
import { usePatient } from "@/shared/api/use-patient";
import { PatientPersonalForm } from "./components/patient-personal-form";

const route = getRouteApi("/_app/patients/$id/edit");

export function PatientEditPage() {
  const { id } = route.useParams();
  const { data, isPending, isError, error } = usePatient(id);

  return (
    <div className="space-y-6">
      <Button asChild variant="ghost" size="sm" className="-ml-2">
        <Link to="/patients/$id" params={{ id }}>
          <ArrowLeft className="size-4" />
          Wróć do profilu
        </Link>
      </Button>
      <PageHeader title="Edycja danych osobowych" />

      {isPending ? (
        <Skeleton className="h-72 w-full" />
      ) : isError || !data ? (
        <p className="text-sm text-destructive">{error?.message ?? "Nie znaleziono pacjenta"}</p>
      ) : (
        <Card>
          <CardContent className="pt-6">
            <PatientPersonalForm patient={data} patientId={id} />
          </CardContent>
        </Card>
      )}
    </div>
  );
}
