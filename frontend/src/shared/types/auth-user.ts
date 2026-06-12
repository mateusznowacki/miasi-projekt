import type { Role } from "./role";

export interface AuthUser {
  userId: string;
  email: string;
  firstName?: string;
  lastName?: string;
  role: Role;
  accessToken: string;
}

export function getAuthDisplayName(auth: AuthUser): string {
  if (auth.firstName && auth.lastName) {
    return `${auth.firstName} ${auth.lastName}`;
  }
  return auth.email;
}

export function getAuthInitials(auth: AuthUser): string {
  if (auth.firstName && auth.lastName) {
    return `${auth.firstName.charAt(0)}${auth.lastName.charAt(0)}`.toUpperCase();
  }
  return auth.email.slice(0, 2).toUpperCase();
}
