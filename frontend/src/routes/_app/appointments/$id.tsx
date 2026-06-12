import { createFileRoute } from "@tanstack/react-router";
import { AppointmentDetailsPage } from "@/features/appointments/pages/appointment-details/appointment-details-page";
import { requireRole } from "@/shared/auth/require-role";

export const Route = createFileRoute("/_app/appointments/$id")({
  beforeLoad: () => {
    requireRole(["patient", "doctor", "admin_staff"]);
  },
  component: AppointmentDetailsPage,
});
