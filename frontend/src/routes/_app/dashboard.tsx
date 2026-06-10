import { createFileRoute } from "@tanstack/react-router";
import { DashboardPage } from "@/features/dashboard/pages/dashboard-page";
import { requireRole } from "@/shared/auth/require-role";

export const Route = createFileRoute("/_app/dashboard")({
  beforeLoad: () => {
    requireRole(["patient", "doctor", "admin_staff", "admin"]);
  },
  component: DashboardPage,
});
