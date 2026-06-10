import { cn } from "@/lib/utils";
import { formatTime } from "@/shared/lib/format-date";
import type { Slot } from "@/shared/types/slot";

export function SlotPicker({
  slots,
  selectedSlotId,
  onSelect,
}: {
  slots: Slot[];
  selectedSlotId?: string | null;
  onSelect?: (slot: Slot) => void;
}) {
  return (
    <div className="grid grid-cols-3 gap-2 sm:grid-cols-4 md:grid-cols-5">
      {slots.map((slot) => {
        const selected = slot.id === selectedSlotId;
        return (
          <button
            key={slot.id}
            type="button"
            onClick={() => onSelect?.(slot)}
            className={cn(
              "rounded-lg border px-2 py-2 text-sm font-medium transition-colors",
              "hover:border-primary/50 hover:bg-accent/50",
              selected && "border-primary bg-primary text-primary-foreground hover:bg-primary",
            )}
          >
            {formatTime(slot.startTime)}
          </button>
        );
      })}
    </div>
  );
}
