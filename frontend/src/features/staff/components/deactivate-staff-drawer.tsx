import { useState } from "react";
import { Ban, Loader2 } from "lucide-react";
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
import { useDeactivateStaff } from "../api/use-deactivate-staff";

export function DeactivateStaffDrawer({ staffId }: { staffId: string }) {
  const [open, setOpen] = useState(false);
  const deactivate = useDeactivateStaff();

  function handleConfirm() {
    deactivate.mutate(staffId, {
      onSuccess: () => {
        toast.success("Konto zostało dezaktywowane");
        setOpen(false);
      },
      onError: (error) => toast.error(error.message),
    });
  }

  return (
    <Drawer open={open} onOpenChange={setOpen}>
      <DrawerTrigger asChild>
        <Button variant="destructive">
          <Ban className="size-4" />
          Dezaktywuj konto
        </Button>
      </DrawerTrigger>
      <DrawerContent>
        <div className="mx-auto w-full max-w-md">
          <DrawerHeader>
            <DrawerTitle>Dezaktywować konto?</DrawerTitle>
            <DrawerDescription>
              Pracownik straci dostęp do systemu, a lekarz nie będzie dostępny do rezerwacji.
            </DrawerDescription>
          </DrawerHeader>
          <DrawerFooter>
            <Button variant="destructive" onClick={handleConfirm} disabled={deactivate.isPending}>
              {deactivate.isPending && <Loader2 className="size-4 animate-spin" />}
              Tak, dezaktywuj
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
