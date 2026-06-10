import { useState } from "react";
import { Link, useNavigate } from "@tanstack/react-router";
import { Activity, Loader2 } from "lucide-react";
import { toast } from "sonner";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Checkbox } from "@/components/ui/checkbox";
import { Input } from "@/components/ui/input";
import { FormField } from "@/shared/components/form-field";
import { useRegister } from "../api/use-register";

const schema = z.object({
  firstName: z.string().min(1, "Podaj imię"),
  lastName: z.string().min(1, "Podaj nazwisko"),
  email: z.string().email("Podaj poprawny adres email"),
  password: z.string().min(6, "Hasło musi mieć co najmniej 6 znaków"),
  pesel: z.string().regex(/^\d{11}$/, "PESEL musi mieć 11 cyfr"),
  phone: z.string().min(7, "Podaj numer telefonu"),
});

const INITIAL = {
  firstName: "",
  lastName: "",
  email: "",
  password: "",
  pesel: "",
  phone: "",
};

export function RegisterPage() {
  const navigate = useNavigate();
  const register = useRegister();
  const [values, setValues] = useState(INITIAL);
  const [acceptTerms, setAcceptTerms] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  function set(field: keyof typeof INITIAL, value: string) {
    setValues((prev) => ({ ...prev, [field]: value }));
  }

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    const result = schema.safeParse(values);
    const nextErrors: Record<string, string> = result.success
      ? {}
      : Object.fromEntries(result.error.issues.map((i) => [i.path[0], i.message]));
    if (!acceptTerms) nextErrors.terms = "Musisz zaakceptować regulamin";
    setErrors(nextErrors);
    if (Object.keys(nextErrors).length > 0 || !result.success) return;

    register.mutate(result.data, {
      onSuccess: () => {
        toast.success("Konto zostało utworzone. Możesz się zalogować.");
        navigate({ to: "/login" });
      },
      onError: (error) => toast.error(error.message),
    });
  }

  return (
    <div className="flex min-h-svh items-center justify-center bg-muted/40 p-4">
      <div className="w-full max-w-md">
        <div className="mb-6 flex flex-col items-center gap-2 text-center">
          <div className="flex size-12 items-center justify-center rounded-xl bg-primary text-primary-foreground">
            <Activity className="size-6" />
          </div>
          <h1 className="text-xl font-semibold tracking-tight">Medflow</h1>
        </div>
        <Card>
          <CardHeader>
            <CardTitle>Rejestracja pacjenta</CardTitle>
            <CardDescription>Załóż konto, aby umawiać wizyty online.</CardDescription>
          </CardHeader>
          <form onSubmit={handleSubmit}>
            <CardContent className="space-y-4">
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
              <FormField label="Hasło" htmlFor="password" error={errors.password}>
                <Input
                  id="password"
                  type="password"
                  value={values.password}
                  onChange={(e) => set("password", e.target.value)}
                />
              </FormField>
              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <FormField label="PESEL" htmlFor="pesel" error={errors.pesel}>
                  <Input
                    id="pesel"
                    inputMode="numeric"
                    value={values.pesel}
                    onChange={(e) => set("pesel", e.target.value)}
                  />
                </FormField>
                <FormField label="Telefon" htmlFor="phone" error={errors.phone}>
                  <Input
                    id="phone"
                    value={values.phone}
                    onChange={(e) => set("phone", e.target.value)}
                  />
                </FormField>
              </div>
              <div className="space-y-1.5">
                <label className="flex items-start gap-2 text-sm">
                  <Checkbox
                    checked={acceptTerms}
                    onCheckedChange={(checked) => setAcceptTerms(checked === true)}
                    className="mt-0.5"
                  />
                  <span className="text-muted-foreground">
                    Akceptuję regulamin i politykę prywatności Medflow.
                  </span>
                </label>
                {errors.terms && <p className="text-xs text-destructive">{errors.terms}</p>}
              </div>
            </CardContent>
            <CardFooter className="mt-4 flex-col gap-3">
              <Button type="submit" className="w-full" disabled={register.isPending}>
                {register.isPending && <Loader2 className="size-4 animate-spin" />}
                Zarejestruj się
              </Button>
              <p className="text-center text-sm text-muted-foreground">
                Masz już konto?{" "}
                <Link to="/login" className="font-medium text-primary hover:underline">
                  Zaloguj się
                </Link>
              </p>
            </CardFooter>
          </form>
        </Card>
      </div>
    </div>
  );
}
