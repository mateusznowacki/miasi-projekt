import { useState } from "react";
import { Link, useNavigate } from "@tanstack/react-router";
import { Activity, Loader2 } from "lucide-react";
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
import { Input } from "@/components/ui/input";
import { FormField } from "@/shared/components/form-field";
import { useLogin } from "../api/use-login";

const schema = z.object({
  email: z.string().email("Podaj poprawny adres email"),
  password: z.string().min(1, "Podaj hasło"),
});

export function LoginPage() {
  const navigate = useNavigate();
  const login = useLogin();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState<Record<string, string>>({});

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    const result = schema.safeParse({ email, password });
    if (!result.success) {
      setErrors(Object.fromEntries(result.error.issues.map((i) => [i.path[0], i.message])));
      return;
    }
    setErrors({});
    login.mutate(result.data, {
      onSuccess: () => navigate({ to: "/dashboard" }),
    });
  }

  return (
    <div className="flex min-h-svh items-center justify-center bg-muted/40 p-4">
      <div className="w-full max-w-sm">
        <div className="mb-6 flex flex-col items-center gap-2 text-center">
          <div className="flex size-12 items-center justify-center rounded-xl bg-primary text-primary-foreground">
            <Activity className="size-6" />
          </div>
          <h1 className="text-xl font-semibold tracking-tight">Medflow</h1>
          <p className="text-sm text-muted-foreground">Twoja opieka medyczna w jednym miejscu</p>
        </div>
        <Card>
          <CardHeader>
            <CardTitle>Zaloguj się</CardTitle>
            <CardDescription>Wprowadź dane logowania, aby kontynuować.</CardDescription>
          </CardHeader>
          <form onSubmit={handleSubmit}>
            <CardContent className="space-y-4">
              <FormField label="Email" htmlFor="email" error={errors.email}>
                <Input
                  id="email"
                  type="email"
                  autoComplete="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </FormField>
              <FormField label="Hasło" htmlFor="password" error={errors.password}>
                <Input
                  id="password"
                  type="password"
                  autoComplete="current-password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
              </FormField>
              {login.isError && (
                <p className="text-sm text-destructive">{login.error.message}</p>
              )}
            </CardContent>
            <CardFooter className="mt-4 flex-col gap-3">
              <Button type="submit" className="w-full" disabled={login.isPending}>
                {login.isPending && <Loader2 className="size-4 animate-spin" />}
                Zaloguj się
              </Button>
              <p className="text-center text-sm text-muted-foreground">
                Nie masz konta?{" "}
                <Link to="/register" className="font-medium text-primary hover:underline">
                  Zarejestruj się
                </Link>
              </p>
            </CardFooter>
          </form>
        </Card>
      </div>
    </div>
  );
}
