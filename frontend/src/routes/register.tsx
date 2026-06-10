import { createFileRoute, redirect } from "@tanstack/react-router";
import { RegisterPage } from "@/features/auth/pages/register-page";
import { getAuth } from "@/shared/auth/auth-store";

export const Route = createFileRoute("/register")({
  beforeLoad: () => {
    if (getAuth()) {
      throw redirect({ to: "/dashboard" });
    }
  },
  component: RegisterPage,
});
