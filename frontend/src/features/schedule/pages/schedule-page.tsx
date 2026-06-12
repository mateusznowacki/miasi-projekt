import { useState } from "react";
import { Calendar } from "@/components/ui/calendar";
import { Card, CardContent } from "@/components/ui/card";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { PageHeader } from "@/shared/components/page-header";
import { useAuth } from "@/shared/auth/use-auth";
import { formatDate } from "@/shared/lib/format-date";
import { useDoctors } from "@/shared/api/use-doctors";
import { AddSlotDrawer } from "../components/add-slot-drawer";
import { ScheduleDayView } from "../components/schedule-day-view";

export function SchedulePage() {
  const auth = useAuth();
  const isStaff = auth?.role === "admin_staff";
  const doctors = useDoctors({});

  const [selectedDoctorId, setSelectedDoctorId] = useState<string>(
    auth?.role === "doctor" ? auth.userId : "",
  );
  const [date, setDate] = useState<Date | undefined>(new Date());

  const fallbackDoctorId = isStaff ? (doctors.data?.[0]?.id ?? "") : "";
  const effectiveDoctorId = selectedDoctorId || fallbackDoctorId;
  const doctorId = effectiveDoctorId || null;

  return (
    <div>
      <PageHeader
        title="Harmonogram"
        description="Zarządzaj dostępnymi terminami wizyt."
        actions={
          doctorId && date ? <AddSlotDrawer doctorId={doctorId} date={date} /> : undefined
        }
      />

      {isStaff && (
        <div className="mb-4 max-w-sm">
          <Select value={effectiveDoctorId} onValueChange={setSelectedDoctorId}>
            <SelectTrigger className="w-full">
              <SelectValue placeholder="Wybierz lekarza" />
            </SelectTrigger>
            <SelectContent>
              {(doctors.data ?? []).map((doctor) => (
                <SelectItem key={doctor.id} value={doctor.id ?? ""}>
                  dr {doctor.firstName} {doctor.lastName} · {doctor.specialization}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      )}

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-[auto_1fr]">
        <Card className="h-fit">
          <CardContent className="p-3">
            <Calendar mode="single" selected={date} onSelect={setDate} />
          </CardContent>
        </Card>

        <div>
          <h2 className="mb-3 font-semibold">
            {date ? formatDate(date.toISOString()) : "Wybierz dzień"}
          </h2>
          {doctorId && date ? (
            <ScheduleDayView doctorId={doctorId} date={date} editable />
          ) : (
            <p className="text-sm text-muted-foreground">
              Wybierz lekarza i dzień, aby zobaczyć harmonogram.
            </p>
          )}
        </div>
      </div>
    </div>
  );
}
