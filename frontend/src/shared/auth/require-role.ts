import { redirect } from "@tanstack/react-router";
import type { Role } from "../types/role";
import { getAuth } from "./auth-store";

export function requireAuth(): NonNullable<ReturnType<typeof getAuth>> {
  const auth = getAuth();
  if (!auth) {
    throw redirect({ to: "/login" });
  }
  return auth;
}

export function requireRole(roles: Role[]): NonNullable<ReturnType<typeof getAuth>> {
  const auth = requireAuth();
  if (!roles.includes(auth.role)) {
    throw redirect({ to: "/dashboard" });
  }
  return auth;
}
