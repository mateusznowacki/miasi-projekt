import { createFileRoute } from "@tanstack/react-router";
import { PatientProfilePage } from "@/features/patients/pages/patient-profile-page";
import { requireRole } from "@/shared/auth/require-role";

export const Route = createFileRoute("/_app/patients/$id/")({
  beforeLoad: () => {
    requireRole(["patient", "doctor", "admin_staff"]);
  },
  component: PatientProfilePage,
});
