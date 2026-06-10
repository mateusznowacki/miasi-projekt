import { createFileRoute } from "@tanstack/react-router";
import { DoctorAvailabilityPage } from "@/features/schedule/pages/doctor-availability-page";
import { requireRole } from "@/shared/auth/require-role";

export const Route = createFileRoute("/_app/schedule/$doctorId")({
  beforeLoad: () => {
    requireRole(["patient", "doctor", "admin_staff"]);
  },
  component: DoctorAvailabilityPage,
});
