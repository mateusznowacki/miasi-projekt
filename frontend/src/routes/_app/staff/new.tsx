import { createFileRoute } from "@tanstack/react-router";
import { StaffCreatePage } from "@/features/staff/pages/staff-create/staff-create-page";
import { requireRole } from "@/shared/auth/require-role";

export const Route = createFileRoute("/_app/staff/new")({
  beforeLoad: () => {
    requireRole(["admin"]);
  },
  component: StaffCreatePage,
});
