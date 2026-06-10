import { useState } from "react";
import { Check, Search, Stethoscope } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import { cn } from "@/lib/utils";
import { EmptyState } from "@/shared/components/empty-state";
import type { StaffMember } from "@/shared/types/staff-member";
import { useDoctors } from "../api/use-doctors";

export function DoctorPicker({
  selectedDoctorId,
  onSelect,
}: {
  selectedDoctorId?: string | null;
  onSelect: (doctor: StaffMember) => void;
}) {
  const [name, setName] = useState("");
  const [specialization, setSpecialization] = useState("");
  const { data, isPending } = useDoctors({ name, specialization });

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="Nazwisko lekarza"
            className="pl-9"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>
        <div className="relative">
          <Stethoscope className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="Specjalizacja"
            className="pl-9"
            value={specialization}
            onChange={(e) => setSpecialization(e.target.value)}
          />
        </div>
      </div>

      {isPending ? (
        <div className="grid grid-cols-1 gap-2 sm:grid-cols-2">
          <Skeleton className="h-16 w-full" />
          <Skeleton className="h-16 w-full" />
        </div>
      ) : data && data.length > 0 ? (
        <div className="grid grid-cols-1 gap-2 sm:grid-cols-2">
          {data.map((doctor) => {
            const selected = doctor.id === selectedDoctorId;
            return (
              <button
                key={doctor.id}
                type="button"
                onClick={() => onSelect(doctor)}
                className={cn(
                  "flex items-center gap-3 rounded-xl border p-3 text-left transition-colors hover:border-primary/50 hover:bg-accent/40",
                  selected && "border-primary ring-1 ring-primary",
                )}
              >
                <div className="flex size-10 shrink-0 items-center justify-center rounded-full bg-primary/10 text-primary">
                  <Stethoscope className="size-5" />
                </div>
                <div className="min-w-0 flex-1">
                  <p className="truncate font-medium">
                    dr {doctor.firstName} {doctor.lastName}
                  </p>
                  <p className="text-sm text-muted-foreground">{doctor.specialization}</p>
                </div>
                {selected && <Check className="size-4 text-primary" />}
              </button>
            );
          })}
        </div>
      ) : (
        <EmptyState icon={Search} title="Brak lekarzy" description="Zmień kryteria wyszukiwania." />
      )}
    </div>
  );
}
