import type { SlotDto } from "@/client";
import type { SlotStatus } from "@/shared/types/slot";

export type SlotInput = {
  startTime: string;
  endTime: string;
  room: string;
};

export function getSlotStartTime(slot: SlotDto): string {
  return slot.timeRange?.startTime ?? "";
}

export function getSlotEndTime(slot: SlotDto): string {
  return slot.timeRange?.endTime ?? "";
}

export function normalizeSlotStatus(status?: string): SlotStatus {
  if (status === "BOOKED" || status === "Zajęty") return "Zajęty";
  return "Wolny";
}

export function isSlotAvailable(status?: string): boolean {
  return status === "AVAILABLE" || status === "Wolny";
}
