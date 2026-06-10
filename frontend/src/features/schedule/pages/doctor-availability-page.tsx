import { useState } from "react";
import { getRouteApi } from "@tanstack/react-router";
import { Calendar } from "@/components/ui/calendar";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { PageHeader } from "@/shared/components/page-header";
import { formatDate } from "@/shared/lib/format-date";
import { useStaffMember } from "@/features/staff/api/use-staff-member";
import { ScheduleDayView } from "../components/schedule-day-view";

const route = getRouteApi("/_app/schedule/$doctorId");

export function DoctorAvailabilityPage() {
  const { doctorId } = route.useParams();
  const [date, setDate] = useState<Date | undefined>(new Date());
  const doctor = useStaffMember(doctorId);

  return (
    <div>
      {doctor.isPending ? (
        <Skeleton className="mb-6 h-9 w-64" />
      ) : (
        <PageHeader
          title={
            doctor.data
              ? `Dostępność: dr ${doctor.data.firstName} ${doctor.data.lastName}`
              : "Dostępność lekarza"
          }
          description={doctor.data?.specialization}
        />
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
          {date && <ScheduleDayView doctorId={doctorId} date={date} editable={false} />}
        </div>
      </div>
    </div>
  );
}
