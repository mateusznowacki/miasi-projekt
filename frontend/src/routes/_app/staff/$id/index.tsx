import { createFileRoute } from "@tanstack/react-router";
import { StaffProfilePage } from "@/features/staff/pages/staff-profile/staff-profile-page";
import { requireRole } from "@/shared/auth/require-role";

export const Route = createFileRoute("/_app/staff/$id/")({
  beforeLoad: () => {
    requireRole(["admin_staff", "admin"]);
  },
  component: StaffProfilePage,
});
