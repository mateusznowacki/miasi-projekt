import { useState } from "react";
import { Loader2, Plus } from "lucide-react";
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
import { combineDateAndTime, formatDate } from "@/shared/lib/format-date";
import { useAddSlots } from "../api/use-add-slots";

export function AddSlotDrawer({ doctorId, date }: { doctorId: string; date: Date }) {
  const [open, setOpen] = useState(false);
  const [start, setStart] = useState("09:00");
  const [end, setEnd] = useState("09:30");
  const [room, setRoom] = useState("Gabinet 1");
  const [error, setError] = useState("");
  const addSlots = useAddSlots();

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    if (end <= start) {
      setError("Godzina zakończenia musi być późniejsza niż rozpoczęcia");
      return;
    }
    setError("");
    addSlots.mutate(
      {
        doctorId,
        slots: [
          {
            startTime: combineDateAndTime(date, start),
            endTime: combineDateAndTime(date, end),
            room,
          },
        ],
      },
      {
        onSuccess: () => {
          toast.success("Termin został dodany");
          setOpen(false);
        },
        onError: (err) => toast.error(err.message),
      },
    );
  }

  return (
    <Drawer open={open} onOpenChange={setOpen}>
      <DrawerTrigger asChild>
        <Button>
          <Plus className="size-4" />
          Dodaj termin
        </Button>
      </DrawerTrigger>
      <DrawerContent>
        <form onSubmit={handleSubmit} className="mx-auto w-full max-w-md">
          <DrawerHeader>
            <DrawerTitle>Dodaj termin</DrawerTitle>
            <DrawerDescription>{formatDate(date.toISOString())}</DrawerDescription>
          </DrawerHeader>
          <div className="space-y-4 px-4">
            <div className="grid grid-cols-2 gap-3">
              <FormField label="Od" htmlFor="start">
                <Input id="start" type="time" value={start} onChange={(e) => setStart(e.target.value)} />
              </FormField>
              <FormField label="Do" htmlFor="end">
                <Input id="end" type="time" value={end} onChange={(e) => setEnd(e.target.value)} />
              </FormField>
            </div>
            <FormField label="Gabinet" htmlFor="room" error={error}>
              <Input id="room" value={room} onChange={(e) => setRoom(e.target.value)} />
            </FormField>
          </div>
          <DrawerFooter>
            <Button type="submit" disabled={addSlots.isPending}>
              {addSlots.isPending && <Loader2 className="size-4 animate-spin" />}
              Zapisz termin
            </Button>
            <DrawerClose asChild>
              <Button type="button" variant="outline">
                Anuluj
              </Button>
            </DrawerClose>
          </DrawerFooter>
        </form>
      </DrawerContent>
    </Drawer>
  );
}
