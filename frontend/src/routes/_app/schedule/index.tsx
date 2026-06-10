import { createFileRoute } from "@tanstack/react-router";
import { SchedulePage } from "@/features/schedule/pages/schedule-page";
import { requireRole } from "@/shared/auth/require-role";

export const Route = createFileRoute("/_app/schedule/")({
  beforeLoad: () => {
    requireRole(["doctor", "admin_staff"]);
  },
  component: SchedulePage,
});
