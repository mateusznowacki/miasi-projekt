import { Link, getRouteApi } from "@tanstack/react-router";
import { ArrowLeft } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { PageHeader } from "@/shared/components/page-header";
import { usePatient } from "../api/use-patient";
import { PatientMedicalForm } from "../components/patient-medical-form";

const route = getRouteApi("/_app/patients/$id/medical");

export function PatientMedicalEditPage() {
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
      <PageHeader title="Edycja danych medycznych" />

      {isPending ? (
        <Skeleton className="h-72 w-full" />
      ) : isError ? (
        <p className="text-sm text-destructive">{error.message}</p>
      ) : (
        <Card>
          <CardContent className="pt-6">
            <PatientMedicalForm patient={data} />
          </CardContent>
        </Card>
      )}
    </div>
  );
}
