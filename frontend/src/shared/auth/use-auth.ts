import { useSyncExternalStore } from "react";
import { getAuth, subscribe } from "./auth-store";

export function useAuth() {
  return useSyncExternalStore(subscribe, getAuth, () => null);
}
