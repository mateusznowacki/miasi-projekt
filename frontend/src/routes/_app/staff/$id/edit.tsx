import { createFileRoute } from "@tanstack/react-router";
import { StaffEditPage } from "@/features/staff/pages/staff-edit/staff-edit-page";
import { requireRole } from "@/shared/auth/require-role";

export const Route = createFileRoute("/_app/staff/$id/edit")({
  beforeLoad: () => {
    requireRole(["admin"]);
  },
  component: StaffEditPage,
});
