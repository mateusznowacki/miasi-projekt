import { createFileRoute } from "@tanstack/react-router";
import { PatientMedicalEditPage } from "@/features/patients/pages/patient-medical-edit-page";
import { requireRole } from "@/shared/auth/require-role";

export const Route = createFileRoute("/_app/patients/$id/medical")({
  beforeLoad: () => {
    requireRole(["doctor", "admin_staff"]);
  },
  component: PatientMedicalEditPage,
});
