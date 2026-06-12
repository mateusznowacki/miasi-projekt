import { createFileRoute } from "@tanstack/react-router";
import { PatientsListPage } from "@/features/patients/pages/patients-list/patients-list-page";
import { requireRole } from "@/shared/auth/require-role";

export const Route = createFileRoute("/_app/patients/")({
  beforeLoad: () => {
    requireRole(["doctor", "admin_staff"]);
  },
  component: PatientsListPage,
});
