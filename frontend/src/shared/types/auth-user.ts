import type { Role } from "./role";

export interface AuthUser {
  userId: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
  accessToken: string;
}
