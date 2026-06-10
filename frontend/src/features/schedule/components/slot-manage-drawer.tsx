import { useState } from "react";
import { Loader2, Pencil, Trash2 } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import {
  Drawer,
  DrawerClose,
  DrawerContent,
  DrawerDescription,
  DrawerFooter,
  DrawerHeader,
  DrawerTitle,
  DrawerTrigger,
} from "@/components/ui/drawer";
import { Input } from "@/components/ui/input";
import { FormField } from "@/shared/components/form-field";
import { SlotStatusBadge } from "@/shared/components/status-badge";
import {
  combineDateAndTime,
  formatTime,
  toTimeInputValue,
} from "@/shared/lib/format-date";
import type { Slot } from "@/shared/types/slot";
import { useDeleteSlot } from "../api/use-delete-slot";
import { useUpdateSlot } from "../api/use-update-slot";

export function SlotManageDrawer({ slot }: { slot: Slot }) {
  const [open, setOpen] = useState(false);
  const [start, setStart] = useState(toTimeInputValue(slot.startTime));
  const [end, setEnd] = useState(toTimeInputValue(slot.endTime));
  const [room, setRoom] = useState(slot.room);
  const [error, setError] = useState("");
  const updateSlot = useUpdateSlot();
  const deleteSlot = useDeleteSlot();
  const baseDate = new Date(slot.startTime);

  function handleSave(event: React.FormEvent) {
    event.preventDefault();
    if (end <= start) {
      setError("Godzina zakończenia musi być późniejsza niż rozpoczęcia");
      return;
    }
    setError("");
    updateSlot.mutate(
      {
        slotId: slot.id,
        data: {
          startTime: combineDateAndTime(baseDate, start),
          endTime: combineDateAndTime(baseDate, end),
          room,
        },
      },
      {
        onSuccess: () => {
          toast.success("Termin zaktualizowany");
          setOpen(false);
        },
        onError: (err) => toast.error(err.message),
      },
    );
  }

  function handleDelete() {
    deleteSlot.mutate(slot.id, {
      onSuccess: () => {
        toast.success("Termin usunięty");
        setOpen(false);
      },
      onError: (err) => toast.error(err.message),
    });
  }

  return (
    <Drawer open={open} onOpenChange={setOpen}>
      <DrawerTrigger asChild>
        <button
          type="button"
          className="flex w-full items-center justify-between rounded-xl border bg-card p-4 text-left transition-colors hover:border-primary/40 hover:bg-accent/40"
        >
          <div>
            <p className="font-medium">
              {formatTime(slot.startTime)} – {formatTime(slot.endTime)}
            </p>
            <p className="text-sm text-muted-foreground">{slot.room}</p>
          </div>
          <div className="flex items-center gap-2">
            <SlotStatusBadge status={slot.status} />
            <Pencil className="size-4 text-muted-foreground" />
          </div>
        </button>
      </DrawerTrigger>
      <DrawerContent>
        <form onSubmit={handleSave} className="mx-auto w-full max-w-md">
          <DrawerHeader>
            <DrawerTitle>Edytuj termin</DrawerTitle>
            <DrawerDescription>Zmień godziny lub usuń wolny termin.</DrawerDescription>
          </DrawerHeader>
          <div className="space-y-4 px-4">
            <div className="grid grid-cols-2 gap-3">
              <FormField label="Od" htmlFor="edit-start">
                <Input
                  id="edit-start"
                  type="time"
                  value={start}
                  onChange={(e) => setStart(e.target.value)}
                />
              </FormField>
              <FormField label="Do" htmlFor="edit-end">
                <Input
                  id="edit-end"
                  type="time"
                  value={end}
                  onChange={(e) => setEnd(e.target.value)}
                />
              </FormField>
            </div>
            <FormField label="Gabinet" htmlFor="edit-room" error={error}>
              <Input id="edit-room" value={room} onChange={(e) => setRoom(e.target.value)} />
            </FormField>
          </div>
          <DrawerFooter>
            <Button type="submit" disabled={updateSlot.isPending}>
              {updateSlot.isPending && <Loader2 className="size-4 animate-spin" />}
              Zapisz zmiany
            </Button>
            <Button
              type="button"
              variant="destructive"
              onClick={handleDelete}
              disabled={deleteSlot.isPending}
            >
              {deleteSlot.isPending ? (
                <Loader2 className="size-4 animate-spin" />
              ) : (
                <Trash2 className="size-4" />
              )}
              Usuń termin
            </Button>
            <DrawerClose asChild>
              <Button type="button" variant="outline">
                Zamknij
              </Button>
            </DrawerClose>
          </DrawerFooter>
        </form>
      </DrawerContent>
    </Drawer>
  );
}
