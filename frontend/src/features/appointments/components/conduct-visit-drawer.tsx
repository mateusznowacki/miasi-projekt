import { useState } from "react";
import { Stethoscope } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import {
  Drawer,
  DrawerContent,
  DrawerDescription,
  DrawerHeader,
  DrawerTitle,
  DrawerTrigger,
} from "@/components/ui/drawer";
import {
  MedicalRecordForm,
  type MedicalRecordFormValues,
} from "@/shared/components/medical-record-form";
import { useCreateMedicalRecord } from "../api/use-create-medical-record";

export function ConductVisitDrawer({ appointmentId }: { appointmentId: string }) {
  const [open, setOpen] = useState(false);
  const createRecord = useCreateMedicalRecord();

  function handleSubmit(values: MedicalRecordFormValues) {
    createRecord.mutate(
      { appointmentId, ...values },
      {
        onSuccess: () => {
          toast.success("Rekord medyczny zapisany, wizyta zakończona");
          setOpen(false);
        },
        onError: (error) => toast.error(error.message),
      },
    );
  }

  return (
    <Drawer open={open} onOpenChange={setOpen}>
      <DrawerTrigger asChild>
        <Button>
          <Stethoscope className="size-4" />
          Przeprowadź wizytę
        </Button>
      </DrawerTrigger>
      <DrawerContent>
        <div className="mx-auto w-full max-w-lg overflow-y-auto px-4 pb-6">
          <DrawerHeader className="px-0">
            <DrawerTitle>Rekord medyczny</DrawerTitle>
            <DrawerDescription>
              Uzupełnij rekord wizyty. Zapisanie zakończy wizytę.
            </DrawerDescription>
          </DrawerHeader>
          <MedicalRecordForm
            onSubmit={handleSubmit}
            isPending={createRecord.isPending}
            onCancel={() => setOpen(false)}
          />
        </div>
      </DrawerContent>
    </Drawer>
  );
}
