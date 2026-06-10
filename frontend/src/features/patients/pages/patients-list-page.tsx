import { useState } from "react";
import { Search, Users } from "lucide-react";
import { Input } from "@/components/ui/input";
import { EmptyState } from "@/shared/components/empty-state";
import { ListSkeleton } from "@/shared/components/list-skeleton";
import { PageHeader } from "@/shared/components/page-header";
import { usePatientsList } from "../api/use-patients-list";
import { PatientCard } from "../components/patient-card";

export function PatientsListPage() {
  const [query, setQuery] = useState("");
  const { data, isPending, isError, error } = usePatientsList({ name: query });

  return (
    <div>
      <PageHeader title="Pacjenci" description="Wyszukaj i przeglądaj kartoteki pacjentów." />

      <div className="relative mb-4 max-w-md">
        <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          placeholder="Szukaj po imieniu lub nazwisku"
          className="pl-9"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
      </div>

      {isPending ? (
        <ListSkeleton count={4} />
      ) : isError ? (
        <p className="text-sm text-destructive">{error.message}</p>
      ) : data.length === 0 ? (
        <EmptyState
          icon={Users}
          title="Brak pacjentów"
          description="Nie znaleziono pacjentów dla podanych kryteriów."
        />
      ) : (
        <div className="flex flex-col gap-3">
          {data.map((patient) => (
            <PatientCard key={patient.id} patient={patient} />
          ))}
        </div>
      )}
    </div>
  );
}
