import { useState } from "react";
import { Search, Users } from "lucide-react";
import { Input } from "@/components/ui/input";
import { EmptyState } from "@/shared/components/empty-state";
import { ListSkeleton } from "@/shared/components/list-skeleton";
import { PageHeader } from "@/shared/components/page-header";
import { useSearchPatients } from "@/shared/api/use-search-patients";
import { PatientCard } from "../../components/patient-card";

export function PatientsListPage() {
  const [query, setQuery] = useState("");
  const { data, isPending, isError, error, isFetching } = useSearchPatients(query);
  const hasQuery = query.trim().length >= 2;

  return (
    <div>
      <PageHeader title="Pacjenci" description="Wyszukaj i przeglądaj kartoteki pacjentów." />

      <div className="relative mb-4 max-w-md">
        <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          placeholder="Szukaj po imieniu lub nazwisku (min. 2 znaki)"
          className="pl-9"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
      </div>

      {!hasQuery ? (
        <EmptyState
          icon={Search}
          title="Wpisz kryteria wyszukiwania"
          description="Podaj imię, nazwisko lub oba, aby znaleźć pacjenta."
        />
      ) : isPending || isFetching ? (
        <ListSkeleton count={4} />
      ) : isError ? (
        <p className="text-sm text-destructive">{error.message}</p>
      ) : !data || data.length === 0 ? (
        <EmptyState
          icon={Users}
          title="Brak pacjentów"
          description="Nie znaleziono pacjentów dla podanych kryteriów."
        />
      ) : (
        <div className="flex flex-col gap-3">
          {data.map((patient) => (
            <PatientCard
              key={patient.id?.value ?? `${patient.firstName}-${patient.lastName}`}
              patient={patient}
            />
          ))}
        </div>
      )}
    </div>
  );
}
