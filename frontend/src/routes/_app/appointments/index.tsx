import { createFileRoute } from "@tanstack/react-router";
import { AppointmentsListPage } from "@/features/appointments/pages/appointments-list/appointments-list-page";
import { requireRole } from "@/shared/auth/require-role";

export const Route = createFileRoute("/_app/appointments/")({
  beforeLoad: () => {
    requireRole(["patient", "doctor", "admin_staff"]);
  },
  component: AppointmentsListPage,
});
