import { createFileRoute } from "@tanstack/react-router";
import { BookAppointmentPage } from "@/features/appointments/pages/book-appointment/book-appointment-page";
import { requireRole } from "@/shared/auth/require-role";

export const Route = createFileRoute("/_app/appointments/new")({
  beforeLoad: () => {
    requireRole(["patient", "admin_staff"]);
  },
  component: BookAppointmentPage,
});
