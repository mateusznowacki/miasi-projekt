import { FileText } from "lucide-react";
import { Skeleton } from "@/components/ui/skeleton";
import { EmptyState } from "@/shared/components/empty-state";
import { formatDateTime } from "@/shared/lib/format-date";
import { useMedicalRecord } from "../api/use-medical-record";
import { RecordField } from "./record-field";

export function MedicalRecordView({ appointmentId }: { appointmentId: string }) {
  const { data, isPending, isError, error } = useMedicalRecord(appointmentId);

  if (isPending) {
    return (
      <div className="space-y-3">
        <Skeleton className="h-4 w-1/3" />
        <Skeleton className="h-12 w-full" />
      </div>
    );
  }

  if (isError) return <p className="text-sm text-destructive">{error.message}</p>;

  if (!data) {
    return (
      <EmptyState
        icon={FileText}
        title="Brak rekordu medycznego"
        description="Dla tej wizyty nie utworzono jeszcze rekordu medycznego."
      />
    );
  }

  return (
    <div className="space-y-4">
      <p className="text-xs text-muted-foreground">
        Utworzono: {formatDateTime(data.createdAt)}
      </p>
      <RecordField label="Objawy" value={data.symptoms} />
      <RecordField label="Rozpoznanie" value={data.diagnoses} />
      <RecordField label="Zalecenia / recepty" value={data.prescriptions} />
      <RecordField label="Notatki" value={data.notes} />
    </div>
  );
}
