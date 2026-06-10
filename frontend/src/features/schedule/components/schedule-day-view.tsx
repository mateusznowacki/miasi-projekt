import { CalendarX2 } from "lucide-react";
import { EmptyState } from "@/shared/components/empty-state";
import { ListSkeleton } from "@/shared/components/list-skeleton";
import { useDaySchedule } from "../api/use-day-schedule";
import { SlotManageDrawer } from "./slot-manage-drawer";
import { StaticSlotRow } from "./static-slot-row";

export function ScheduleDayView({
  doctorId,
  date,
  editable,
}: {
  doctorId: string;
  date: Date;
  editable: boolean;
}) {
  const { data, isPending, isError, error } = useDaySchedule(doctorId, date);

  if (isPending) return <ListSkeleton count={3} />;
  if (isError) return <p className="text-sm text-destructive">{error.message}</p>;

  if (data.length === 0) {
    return (
      <EmptyState
        icon={CalendarX2}
        title="Brak terminów"
        description="Dla wybranego dnia nie ma terminów w harmonogramie."
      />
    );
  }

  return (
    <div className="flex flex-col gap-2">
      {data.map((slot) =>
        editable && slot.status === "Wolny" ? (
          <SlotManageDrawer key={slot.id} slot={slot} />
        ) : (
          <StaticSlotRow key={slot.id} slot={slot} />
        ),
      )}
    </div>
  );
}
