export type Role = "patient" | "doctor" | "admin_staff" | "admin";

export const ROLE_LABELS: Record<Role, string> = {
  patient: "Pacjent",
  doctor: "Lekarz",
  admin_staff: "Pracownik administracyjny",
  admin: "Administrator systemu",
};
