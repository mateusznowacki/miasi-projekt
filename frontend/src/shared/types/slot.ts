export type SlotStatus = "Wolny" | "Zajęty";

export interface Slot {
  id: string;
  doctorId: string;
  startTime: string;
  endTime: string;
  status: SlotStatus;
  room: string;
}
