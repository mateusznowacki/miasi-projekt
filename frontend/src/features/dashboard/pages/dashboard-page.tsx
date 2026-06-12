import { useAuth } from "@/shared/auth/use-auth";
import { getAuthDisplayName } from "@/shared/types/auth-user";
import { ROLE_LABELS } from "@/shared/types/role";
import { QuickLinks } from "../components/quick-links";
import { DashboardUpcoming } from "../components/dashboard-upcoming";

export function DashboardPage() {
  const auth = useAuth();
  if (!auth) return null;

  const showUpcoming = auth.role === "patient" || auth.role === "doctor";

  return (
    <div className="space-y-8">
      <section className="rounded-2xl border bg-linear-to-br from-primary/10 via-card to-card p-6">
        <p className="text-sm font-medium text-primary">{ROLE_LABELS[auth.role]}</p>
        <h1 className="mt-1 text-2xl font-semibold tracking-tight">
          Witaj, {getAuthDisplayName(auth)}
        </h1>
        <p className="mt-2 max-w-prose text-sm text-muted-foreground">
          Zarządzaj wizytami, harmonogramem i danymi pacjentów w jednym miejscu.
        </p>
      </section>

      <section>
        <h2 className="mb-3 text-lg font-semibold">Szybkie akcje</h2>
        <QuickLinks role={auth.role} />
      </section>

      {showUpcoming && (
        <section>
          <h2 className="mb-3 text-lg font-semibold">Nadchodzące wizyty</h2>
          <DashboardUpcoming user={auth} />
        </section>
      )}
    </div>
  );
}
