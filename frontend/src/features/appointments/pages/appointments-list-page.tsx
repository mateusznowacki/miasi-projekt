import { Link } from "@tanstack/react-router";
import { CalendarPlus } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { PageHeader } from "@/shared/components/page-header";
import { useAuth } from "@/shared/auth/use-auth";
import { AppointmentsTab } from "../components/appointments-tab";

export function AppointmentsListPage() {
  const auth = useAuth();
  if (!auth) return null;

  const canBook = auth.role === "patient" || auth.role === "admin_staff";

  return (
    <div>
      <PageHeader
        title="Wizyty"
        description="Przeglądaj nadchodzące i przeszłe wizyty."
        actions={
          canBook ? (
            <Button asChild>
              <Link to="/appointments/new">
                <CalendarPlus className="size-4" />
                Umów wizytę
              </Link>
            </Button>
          ) : undefined
        }
      />
      <Tabs defaultValue="upcoming">
        <TabsList>
          <TabsTrigger value="upcoming">Nadchodzące</TabsTrigger>
          <TabsTrigger value="past">Przeszłe</TabsTrigger>
        </TabsList>
        <TabsContent value="upcoming" className="mt-4">
          <AppointmentsTab user={auth} filter="upcoming" />
        </TabsContent>
        <TabsContent value="past" className="mt-4">
          <AppointmentsTab user={auth} filter="past" />
        </TabsContent>
      </Tabs>
    </div>
  );
}
