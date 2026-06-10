import { createFileRoute } from "@tanstack/react-router";
import { AppShell } from "@/shared/components/app-shell";
import { requireAuth } from "@/shared/auth/require-role";

export const Route = createFileRoute("/_app")({
  beforeLoad: () => {
    requireAuth();
  },
  component: AppShell,
});
