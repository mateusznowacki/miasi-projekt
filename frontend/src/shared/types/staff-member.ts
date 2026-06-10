export type StaffRole = "doctor" | "admin_staff";

export interface StaffMember {
  id: string;
  role: StaffRole;
  firstName: string;
  lastName: string;
  email: string;
  active: boolean;
  specialization?: string;
  pwz?: string;
  department?: string;
  position?: string;
}
