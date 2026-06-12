import type { StaffRole } from "@/shared/types/staff-member";
import { ROLE_LABELS } from "@/shared/types/role";

const BACKEND_STAFF_ROLE_MAP: Record<string, StaffRole> = {
  DOCTOR: "doctor",
  doctor: "doctor",
  ADMIN_STAFF: "admin_staff",
  admin_staff: "admin_staff",
};

export function mapStaffRole(role?: string): StaffRole | null {
  return role ? (BACKEND_STAFF_ROLE_MAP[role] ?? null) : null;
}

export function getStaffRoleLabel(role?: string): string {
  const mapped = mapStaffRole(role);
  return mapped ? ROLE_LABELS[mapped] : (role ?? "—");
}
