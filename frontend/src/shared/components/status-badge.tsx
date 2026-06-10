import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";
import type { AppointmentStatus } from "@/shared/types/appointment";
import type { SlotStatus } from "@/shared/types/slot";

const APPOINTMENT_STYLES: Record<AppointmentStatus, string> = {
  Zarezerwowana: "bg-primary/10 text-primary",
  Zakończona: "bg-emerald-500/10 text-emerald-600 dark:text-emerald-400",
  Anulowana: "bg-destructive/10 text-destructive",
};

const SLOT_STYLES: Record<SlotStatus, string> = {
  Wolny: "bg-emerald-500/10 text-emerald-600 dark:text-emerald-400",
  Zajęty: "bg-muted text-muted-foreground",
};

export function AppointmentStatusBadge({ status }: { status: AppointmentStatus }) {
  return <Badge className={cn("border-transparent", APPOINTMENT_STYLES[status])}>{status}</Badge>;
}

export function SlotStatusBadge({ status }: { status: SlotStatus }) {
  return <Badge className={cn("border-transparent", SLOT_STYLES[status])}>{status}</Badge>;
}
