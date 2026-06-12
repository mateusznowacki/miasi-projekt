import { useState } from "react";
import { Link, useNavigate } from "@tanstack/react-router";
import { Loader2 } from "lucide-react";
import { toast } from "sonner";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import type { StaffDto } from "@/client";
import { FormField } from "@/shared/components/form-field";
import type { StaffRole } from "@/shared/types/staff-member";
import { mapStaffRole } from "@/shared/types/map-staff-role";
import { useCreateStaff } from "../pages/staff-create/api/use-create-staff";
import { useUpdateStaff } from "../pages/staff-edit/api/use-update-staff";

const schema = z
  .object({
    role: z.enum(["doctor", "admin_staff"]),
    firstName: z.string().min(1, "Podaj imię"),
    lastName: z.string().min(1, "Podaj nazwisko"),
    email: z.string().email("Podaj poprawny adres email"),
    specialization: z.string(),
    pwz: z.string(),
    department: z.string(),
    position: z.string(),
  })
  .superRefine((data, ctx) => {
    if (data.role === "doctor") {
      if (!data.specialization)
        ctx.addIssue({ code: "custom", path: ["specialization"], message: "Podaj specjalizację" });
      if (!data.pwz) ctx.addIssue({ code: "custom", path: ["pwz"], message: "Podaj numer PWZ" });
    } else if (!data.position) {
      ctx.addIssue({ code: "custom", path: ["position"], message: "Podaj stanowisko" });
    }
  });

function initialValues(member?: StaffDto) {
  return {
    role: mapStaffRole(member?.role) ?? ("doctor" as StaffRole),
    firstName: member?.firstName ?? "",
    lastName: member?.lastName ?? "",
    email: member?.email ?? "",
    specialization: member?.specialization ?? "",
    pwz: member?.pwz ?? "",
    department: member?.department ?? "",
    position: member?.position ?? "",
  };
}

export function StaffForm({ initial }: { initial?: StaffDto }) {
  const navigate = useNavigate();
  const createStaff = useCreateStaff();
  const updateStaff = useUpdateStaff(initial?.id ?? "");
  const [values, setValues] = useState(() => initialValues(initial));
  const [errors, setErrors] = useState<Record<string, string>>({});

  function set(field: keyof ReturnType<typeof initialValues>, value: string) {
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
    const payload =
      result.data.role === "doctor"
        ? {
            role: "doctor" as const,
            firstName: result.data.firstName,
            lastName: result.data.lastName,
            email: result.data.email,
            specialization: result.data.specialization,
            pwz: result.data.pwz,
            department: result.data.department,
          }
        : {
            role: "admin_staff" as const,
            firstName: result.data.firstName,
            lastName: result.data.lastName,
            email: result.data.email,
            position: result.data.position,
          };

    if (initial) {
      updateStaff.mutate(payload, {
        onSuccess: () => {
          toast.success("Dane pracownika zaktualizowane");
          navigate({ to: "/staff/$id", params: { id: initial.id ?? "" } });
        },
        onError: (error) => toast.error(error.message),
      });
    } else {
      createStaff.mutate(payload, {
        onSuccess: (res) => {
          toast.success("Pracownik został dodany");
          navigate({ to: "/staff/$id", params: { id: res.staffId } });
        },
        onError: (error) => toast.error(error.message),
      });
    }
  }

  const isPending = createStaff.isPending || updateStaff.isPending;

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <FormField label="Typ pracownika" htmlFor="role">
        <Select
          value={values.role}
          onValueChange={(value) => set("role", value)}
          disabled={Boolean(initial)}
        >
          <SelectTrigger className="w-full sm:w-72">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="doctor">Lekarz</SelectItem>
            <SelectItem value="admin_staff">Pracownik administracyjny</SelectItem>
          </SelectContent>
        </Select>
      </FormField>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <FormField label="Imię" htmlFor="firstName" error={errors.firstName}>
          <Input
            id="firstName"
            value={values.firstName}
            onChange={(e) => set("firstName", e.target.value)}
          />
        </FormField>
        <FormField label="Nazwisko" htmlFor="lastName" error={errors.lastName}>
          <Input
            id="lastName"
            value={values.lastName}
            onChange={(e) => set("lastName", e.target.value)}
          />
        </FormField>
      </div>

      <FormField label="Email" htmlFor="email" error={errors.email}>
        <Input
          id="email"
          type="email"
          value={values.email}
          onChange={(e) => set("email", e.target.value)}
        />
      </FormField>

      {values.role === "doctor" ? (
        <>
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <FormField label="Specjalizacja" htmlFor="specialization" error={errors.specialization}>
              <Input
                id="specialization"
                value={values.specialization}
                onChange={(e) => set("specialization", e.target.value)}
              />
            </FormField>
            <FormField label="Numer PWZ" htmlFor="pwz" error={errors.pwz}>
              <Input id="pwz" value={values.pwz} onChange={(e) => set("pwz", e.target.value)} />
            </FormField>
          </div>
          <FormField label="Oddział" htmlFor="department">
            <Input
              id="department"
              value={values.department}
              onChange={(e) => set("department", e.target.value)}
            />
          </FormField>
        </>
      ) : (
        <FormField label="Stanowisko" htmlFor="position" error={errors.position}>
          <Input
            id="position"
            value={values.position}
            onChange={(e) => set("position", e.target.value)}
          />
        </FormField>
      )}

      <div className="flex justify-end gap-2">
        <Button asChild type="button" variant="outline">
          <Link to="/staff">Anuluj</Link>
        </Button>
        <Button type="submit" disabled={isPending}>
          {isPending && <Loader2 className="size-4 animate-spin" />}
          Zapisz
        </Button>
      </div>
    </form>
  );
}
