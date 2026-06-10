import { useState } from "react";
import { useNavigate } from "@tanstack/react-router";
import { CalendarClock, Loader2 } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import { PageHeader } from "@/shared/components/page-header";
import { SlotPicker } from "@/shared/components/slot-picker";
import { useAuth } from "@/shared/auth/use-auth";
import { formatDate, formatTime } from "@/shared/lib/format-date";
import type { Slot } from "@/shared/types/slot";
import type { StaffMember } from "@/shared/types/staff-member";
import { usePatientsList } from "@/features/patients/api/use-patients-list";
import { useAvailableSlots } from "../api/use-available-slots";
import { useCreateAppointment } from "../api/use-create-appointment";
import { DoctorPicker } from "../components/doctor-picker";
import { StepCard } from "../components/step-card";
import { SummaryRow } from "../components/summary-row";

const APPOINTMENT_TYPES = ["Konsultacja", "Kontrola", "Badanie"];

export function BookAppointmentPage() {
  const auth = useAuth();
  const navigate = useNavigate();
  const create = useCreateAppointment();
  const isStaff = auth?.role === "admin_staff";

  const [doctor, setDoctor] = useState<StaffMember | null>(null);
  const [date, setDate] = useState<Date | undefined>(undefined);
  const [slot, setSlot] = useState<Slot | null>(null);
  const [type, setType] = useState(APPOINTMENT_TYPES[0]);
  const [patientId, setPatientId] = useState(auth?.role === "patient" ? auth.userId : "");

  const patients = usePatientsList(isStaff ? {} : { name: "__none__" });
  const slots = useAvailableSlots(doctor?.id ?? null, date ? date.toISOString() : null);

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const canConfirm = Boolean(doctor && slot && patientId && type);

  function handleConfirm() {
    if (!doctor || !slot) return;
    create.mutate(
      { doctorId: doctor.id, slotIds: [slot.id], patientId, type },
      {
        onSuccess: (res) => {
          toast.success("Wizyta została zarezerwowana");
          navigate({ to: "/appointments/$id", params: { id: res.appointmentId } });
        },
        onError: (error) => toast.error(error.message),
      },
    );
  }

  return (
    <div className="space-y-6">
      <PageHeader title="Umów wizytę" description="Wybierz lekarza, dzień i dogodny termin." />

      {isStaff && (
        <StepCard step={0} title="Wybierz pacjenta">
          <Select value={patientId} onValueChange={setPatientId}>
            <SelectTrigger className="w-full sm:w-80">
              <SelectValue placeholder="Wybierz pacjenta" />
            </SelectTrigger>
            <SelectContent>
              {(patients.data ?? []).map((p) => (
                <SelectItem key={p.id} value={p.id}>
                  {p.personalData.firstName} {p.personalData.lastName} · {p.personalData.pesel}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </StepCard>
      )}

      <StepCard step={1} title="Wybierz lekarza">
        <DoctorPicker
          selectedDoctorId={doctor?.id}
          onSelect={(d) => {
            setDoctor(d);
            setDate(undefined);
            setSlot(null);
          }}
        />
      </StepCard>

      {doctor && (
        <StepCard step={2} title="Wybierz dzień">
          <div className="flex justify-center sm:justify-start">
            <Calendar
              mode="single"
              selected={date}
              onSelect={(d) => {
                setDate(d);
                setSlot(null);
              }}
              disabled={{ before: today }}
              className="rounded-lg border"
            />
          </div>
        </StepCard>
      )}

      {doctor && date && (
        <StepCard step={3} title={`Dostępne terminy · ${formatDate(date.toISOString())}`}>
          {slots.isPending ? (
            <Skeleton className="h-10 w-full" />
          ) : slots.data && slots.data.length > 0 ? (
            <SlotPicker
              slots={slots.data}
              selectedSlotId={slot?.id}
              onSelect={(s) => setSlot(s)}
            />
          ) : (
            <p className="text-sm text-muted-foreground">
              Brak wolnych terminów w wybranym dniu. Wybierz inny dzień.
            </p>
          )}
        </StepCard>
      )}

      {slot && (
        <StepCard step={4} title="Typ wizyty i potwierdzenie">
          <div className="flex flex-col gap-4 sm:flex-row sm:items-end">
            <div className="space-y-1.5">
              <p className="text-sm font-medium">Typ wizyty</p>
              <Select value={type} onValueChange={setType}>
                <SelectTrigger className="w-full sm:w-56">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {APPOINTMENT_TYPES.map((t) => (
                    <SelectItem key={t} value={t}>
                      {t}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <Drawer>
              <DrawerTrigger asChild>
                <Button disabled={!canConfirm} className="sm:ml-auto">
                  <CalendarClock className="size-4" />
                  Potwierdź rezerwację
                </Button>
              </DrawerTrigger>
              <DrawerContent>
                <div className="mx-auto w-full max-w-md">
                  <DrawerHeader>
                    <DrawerTitle>Potwierdź rezerwację</DrawerTitle>
                    <DrawerDescription>Sprawdź szczegóły przed potwierdzeniem.</DrawerDescription>
                  </DrawerHeader>
                  <div className="space-y-2 px-4 text-sm">
                    <SummaryRow label="Lekarz" value={`dr ${doctor?.firstName} ${doctor?.lastName}`} />
                    <SummaryRow label="Specjalizacja" value={doctor?.specialization ?? "—"} />
                    <SummaryRow label="Termin" value={`${formatDate(slot.startTime)}, ${formatTime(slot.startTime)}`} />
                    <SummaryRow label="Gabinet" value={slot.room} />
                    <SummaryRow label="Typ" value={type} />
                  </div>
                  <DrawerFooter>
                    <Button onClick={handleConfirm} disabled={create.isPending}>
                      {create.isPending && <Loader2 className="size-4 animate-spin" />}
                      Zarezerwuj
                    </Button>
                    <DrawerClose asChild>
                      <Button variant="outline">Anuluj</Button>
                    </DrawerClose>
                  </DrawerFooter>
                </div>
              </DrawerContent>
            </Drawer>
          </div>
        </StepCard>
      )}
    </div>
  );
}
