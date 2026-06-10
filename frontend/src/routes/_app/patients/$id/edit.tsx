import { createFileRoute } from "@tanstack/react-router";
import { PatientEditPage } from "@/features/patients/pages/patient-edit-page";
import { requireRole } from "@/shared/auth/require-role";

export const Route = createFileRoute("/_app/patients/$id/edit")({
  beforeLoad: () => {
    requireRole(["patient", "admin_staff"]);
  },
  component: PatientEditPage,
});
