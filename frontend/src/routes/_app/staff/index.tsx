import { createFileRoute } from "@tanstack/react-router";
import { StaffListPage } from "@/features/staff/pages/staff-list/staff-list-page";
import { requireRole } from "@/shared/auth/require-role";

export const Route = createFileRoute("/_app/staff/")({
  beforeLoad: () => {
    requireRole(["admin_staff", "admin"]);
  },
  component: StaffListPage,
});
