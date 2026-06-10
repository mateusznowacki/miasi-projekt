import { useState } from "react";
import { Link, useNavigate } from "@tanstack/react-router";
import { Loader2 } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { FormField } from "@/shared/components/form-field";
import type { Patient } from "@/shared/types/patient";
import { useUpdatePatientMedical } from "../api/use-update-patient-medical";

export function PatientMedicalForm({ patient }: { patient: Patient }) {
  const navigate = useNavigate();
  const update = useUpdatePatientMedical(patient.id);
  const [values, setValues] = useState(patient.medicalData);

  function set(field: keyof typeof values, value: string) {
    setValues((prev) => ({ ...prev, [field]: value }));
  }

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    update.mutate(values, {
      onSuccess: () => {
        toast.success("Dane medyczne zaktualizowane");
        navigate({ to: "/patients/$id", params: { id: patient.id } });
      },
      onError: (error) => toast.error(error.message),
    });
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <FormField label="Grupa krwi" htmlFor="bloodType">
        <Input
          id="bloodType"
          value={values.bloodType}
          onChange={(e) => set("bloodType", e.target.value)}
        />
      </FormField>
      <FormField label="Alergie" htmlFor="allergies">
        <Textarea
          id="allergies"
          rows={2}
          value={values.allergies}
          onChange={(e) => set("allergies", e.target.value)}
        />
      </FormField>
      <FormField label="Choroby przewlekłe" htmlFor="chronicDiseases">
        <Textarea
          id="chronicDiseases"
          rows={2}
          value={values.chronicDiseases}
          onChange={(e) => set("chronicDiseases", e.target.value)}
        />
      </FormField>
      <FormField label="Leki" htmlFor="medications">
        <Textarea
          id="medications"
          rows={2}
          value={values.medications}
          onChange={(e) => set("medications", e.target.value)}
        />
      </FormField>
      <div className="flex justify-end gap-2">
        <Button asChild type="button" variant="outline">
          <Link to="/patients/$id" params={{ id: patient.id }}>
            Anuluj
          </Link>
        </Button>
        <Button type="submit" disabled={update.isPending}>
          {update.isPending && <Loader2 className="size-4 animate-spin" />}
          Zapisz
        </Button>
      </div>
    </form>
  );
}
