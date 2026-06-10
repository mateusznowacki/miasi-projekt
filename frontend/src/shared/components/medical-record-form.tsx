import { useState } from "react";
import { Loader2 } from "lucide-react";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { FormField } from "./form-field";

export interface MedicalRecordFormValues {
  symptoms: string;
  diagnoses: string;
  prescriptions: string;
  notes: string;
}

const schema = z.object({
  symptoms: z.string().min(1, "Podaj objawy"),
  diagnoses: z.string().min(1, "Podaj rozpoznanie"),
  prescriptions: z.string(),
  notes: z.string(),
});

export function MedicalRecordForm({
  onSubmit,
  isPending,
  onCancel,
}: {
  onSubmit: (values: MedicalRecordFormValues) => void;
  isPending?: boolean;
  onCancel?: () => void;
}) {
  const [values, setValues] = useState<MedicalRecordFormValues>({
    symptoms: "",
    diagnoses: "",
    prescriptions: "",
    notes: "",
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  function set(field: keyof MedicalRecordFormValues, value: string) {
    setValues((prev) => ({ ...prev, [field]: value }));
  }

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    const result = schema.safeParse(values);
    if (!result.success) {
      setErrors(Object.fromEntries(result.error.issues.map((i) => [i.path[0], i.message])));
      return;
    }
    setErrors({});
    onSubmit(result.data);
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <FormField label="Objawy" htmlFor="symptoms" error={errors.symptoms}>
        <Textarea
          id="symptoms"
          rows={3}
          value={values.symptoms}
          onChange={(e) => set("symptoms", e.target.value)}
        />
      </FormField>
      <FormField label="Rozpoznanie" htmlFor="diagnoses" error={errors.diagnoses}>
        <Textarea
          id="diagnoses"
          rows={3}
          value={values.diagnoses}
          onChange={(e) => set("diagnoses", e.target.value)}
        />
      </FormField>
      <FormField label="Zalecenia / recepty" htmlFor="prescriptions" error={errors.prescriptions}>
        <Textarea
          id="prescriptions"
          rows={3}
          value={values.prescriptions}
          onChange={(e) => set("prescriptions", e.target.value)}
        />
      </FormField>
      <FormField label="Notatki" htmlFor="notes" error={errors.notes}>
        <Textarea
          id="notes"
          rows={2}
          value={values.notes}
          onChange={(e) => set("notes", e.target.value)}
        />
      </FormField>
      <div className="flex justify-end gap-2">
        {onCancel && (
          <Button type="button" variant="outline" onClick={onCancel}>
            Anuluj
          </Button>
        )}
        <Button type="submit" disabled={isPending}>
          {isPending && <Loader2 className="size-4 animate-spin" />}
          Zakończ wizytę
        </Button>
      </div>
    </form>
  );
}
