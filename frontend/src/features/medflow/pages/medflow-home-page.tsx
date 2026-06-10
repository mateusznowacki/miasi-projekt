import { MedflowHero } from "../components/medflow-hero";
import { UpcomingVisits } from "../components/upcoming-visits";

export function MedflowHomePage() {
  return (
    <main className="mx-auto flex max-w-2xl flex-col gap-8 p-6">
      <MedflowHero />
      <section>
        <h2 className="mb-4 text-lg font-semibold">Nadchodzące wizyty</h2>
        <UpcomingVisits />
      </section>
    </main>
  );
}
