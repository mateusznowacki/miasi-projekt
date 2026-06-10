import { useState } from "react";
import { Loader2, X } from "lucide-react";
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
import { useCancelAppointment } from "../api/use-cancel-appointment";

export function CancelAppointmentDrawer({ appointmentId }: { appointmentId: string }) {
  const [open, setOpen] = useState(false);
  const cancel = useCancelAppointment();

  function handleConfirm() {
    cancel.mutate(appointmentId, {
      onSuccess: () => {
        toast.success("Wizyta została anulowana");
        setOpen(false);
      },
      onError: (error) => toast.error(error.message),
    });
  }

  return (
    <Drawer open={open} onOpenChange={setOpen}>
      <DrawerTrigger asChild>
        <Button variant="destructive">
          <X className="size-4" />
          Anuluj wizytę
        </Button>
      </DrawerTrigger>
      <DrawerContent>
        <div className="mx-auto w-full max-w-md">
          <DrawerHeader>
            <DrawerTitle>Anulować wizytę?</DrawerTitle>
            <DrawerDescription>
              Termin zostanie zwolniony i ponownie dostępny do rezerwacji. Tej operacji nie można
              cofnąć.
            </DrawerDescription>
          </DrawerHeader>
          <DrawerFooter>
            <Button
              variant="destructive"
              onClick={handleConfirm}
              disabled={cancel.isPending}
            >
              {cancel.isPending && <Loader2 className="size-4 animate-spin" />}
              Tak, anuluj wizytę
            </Button>
            <DrawerClose asChild>
              <Button variant="outline">Wróć</Button>
            </DrawerClose>
          </DrawerFooter>
        </div>
      </DrawerContent>
    </Drawer>
  );
}
