import { Button } from "@/components/ui/button";

export function MedflowHero() {
  return (
    <section className="rounded-xl border bg-card p-6 text-card-foreground">
      <p className="text-sm font-medium text-primary">Medflow</p>
      <h1 className="mt-1 text-2xl font-semibold tracking-tight">
        Twoja opieka medyczna w jednym miejscu
      </h1>
      <p className="mt-2 max-w-prose text-sm text-muted-foreground">
        Umawiaj wizyty, sprawdzaj historię leczenia i zarządzaj pakietem
        medycznym — podobnie jak w systemach prywatnej opieki zdrowotnej.
      </p>
      <Button className="mt-4" type="button">
        Umów wizytę
      </Button>
    </section>
  );
}
