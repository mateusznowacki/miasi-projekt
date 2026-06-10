import type { AuthUser } from "../types/auth-user";

const STORAGE_KEY = "medflow.auth";

let current: AuthUser | null = readFromStorage();
const listeners = new Set<() => void>();

function readFromStorage(): AuthUser | null {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? (JSON.parse(raw) as AuthUser) : null;
  } catch {
    return null;
  }
}

function emit() {
  for (const listener of listeners) listener();
}

export function getAuth(): AuthUser | null {
  return current;
}

export function setAuth(user: AuthUser): void {
  current = user;
  localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
  emit();
}

export function clearAuth(): void {
  current = null;
  localStorage.removeItem(STORAGE_KEY);
  emit();
}

export function subscribe(listener: () => void): () => void {
  listeners.add(listener);
  return () => listeners.delete(listener);
}
