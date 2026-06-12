import type { AuthResult } from "@/client";
import type { AuthUser } from "@/shared/types/auth-user";
import type { Role } from "@/shared/types/role";

const BACKEND_ROLE_MAP: Record<string, Role> = {
  PATIENT: "patient",
  DOCTOR: "doctor",
  ADMIN_STAFF: "admin_staff",
  ADMIN: "admin",
};

export function mapAuthResult(result: AuthResult): AuthUser {
  const role = BACKEND_ROLE_MAP[result.role ?? ""];
  if (!role) {
    throw new Error("Nieznana rola użytkownika");
  }

  return {
    userId: result.userId ?? "",
    email: result.email ?? "",
    role,
    accessToken: result.accessToken ?? "",
  };
}
