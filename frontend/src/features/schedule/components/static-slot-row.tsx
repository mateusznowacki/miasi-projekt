import type { SlotDto } from "@/client";
import { SlotStatusBadge } from "@/shared/components/status-badge";
import { formatTime } from "@/shared/lib/format-date";
import { getSlotEndTime, getSlotStartTime, normalizeSlotStatus } from "../lib/slot-helpers";

export function StaticSlotRow({ slot }: { slot: SlotDto }) {
  const startTime = getSlotStartTime(slot);
  const endTime = getSlotEndTime(slot);

  return (
    <div className="flex items-center justify-between rounded-xl border bg-card p-4">
      <div>
        <p className="font-medium">
          {formatTime(startTime)} – {formatTime(endTime)}
        </p>
        <p className="text-sm text-muted-foreground">{slot.office ?? "—"}</p>
      </div>
      <SlotStatusBadge status={normalizeSlotStatus(slot.status)} />
    </div>
  );
}
