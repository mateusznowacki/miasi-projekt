import { createFileRoute, redirect } from "@tanstack/react-router";
import { LoginPage } from "@/features/auth/pages/login-page";
import { getAuth } from "@/shared/auth/auth-store";

export const Route = createFileRoute("/login")({
  beforeLoad: () => {
    if (getAuth()) {
      throw redirect({ to: "/dashboard" });
    }
  },
  component: LoginPage,
});
