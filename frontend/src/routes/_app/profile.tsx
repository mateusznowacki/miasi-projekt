import { createFileRoute } from "@tanstack/react-router";
import { ProfilePage } from "@/features/profile/pages/profile-page";
import { requireRole } from "@/shared/auth/require-role";

export const Route = createFileRoute("/_app/profile")({
  beforeLoad: () => {
    requireRole(["patient", "doctor", "admin_staff"]);
  },
  component: ProfilePage,
});
