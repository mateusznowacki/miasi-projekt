import { SlotStatusBadge } from "@/shared/components/status-badge";
import { formatTime } from "@/shared/lib/format-date";
import type { Slot } from "@/shared/types/slot";

export function StaticSlotRow({ slot }: { slot: Slot }) {
  return (
    <div className="flex items-center justify-between rounded-xl border bg-card p-4">
      <div>
        <p className="font-medium">
          {formatTime(slot.startTime)} – {formatTime(slot.endTime)}
        </p>
        <p className="text-sm text-muted-foreground">{slot.room}</p>
      </div>
      <SlotStatusBadge status={slot.status} />
    </div>
  );
}
