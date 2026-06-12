import { useState } from "react";
import { Link, useNavigate } from "@tanstack/react-router";
import { Loader2 } from "lucide-react";
import { toast } from "sonner";
import type { Patient } from "@/client";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { FormField } from "@/shared/components/form-field";
import { useUpdatePatientPersonal } from "../api/use-update-patient-personal";

export function PatientPersonalForm({
  patient,
  patientId,
}: {
  patient: Patient;
  patientId: string;
}) {
  const navigate = useNavigate();
  const update = useUpdatePatientPersonal(patientId);
  const [values, setValues] = useState({
    firstName: patient.firstName ?? "",
    lastName: patient.lastName ?? "",
    email: patient.email ?? "",
    phone: patient.phone ?? "",
    address: patient.address ?? "",
  });

  function set(field: keyof typeof values, value: string) {
    setValues((prev) => ({ ...prev, [field]: value }));
  }

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    update.mutate(values, {
      onSuccess: () => {
        toast.success("Dane osobowe zaktualizowane");
        navigate({ to: "/patients/$id", params: { id: patientId } });
      },
      onError: (err) => toast.error(err.message),
    });
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <FormField label="Imię" htmlFor="firstName">
          <Input
            id="firstName"
            value={values.firstName}
            onChange={(e) => set("firstName", e.target.value)}
          />
        </FormField>
        <FormField label="Nazwisko" htmlFor="lastName">
          <Input
            id="lastName"
            value={values.lastName}
            onChange={(e) => set("lastName", e.target.value)}
          />
        </FormField>
      </div>
      <FormField label="Email" htmlFor="email">
        <Input
          id="email"
          type="email"
          value={values.email}
          onChange={(e) => set("email", e.target.value)}
        />
      </FormField>
      <FormField label="Telefon" htmlFor="phone">
        <Input id="phone" value={values.phone} onChange={(e) => set("phone", e.target.value)} />
      </FormField>
      <FormField label="Adres" htmlFor="address">
        <Input id="address" value={values.address} onChange={(e) => set("address", e.target.value)} />
      </FormField>
      <div className="flex justify-end gap-2">
        <Button asChild type="button" variant="outline">
          <Link to="/patients/$id" params={{ id: patientId }}>
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
