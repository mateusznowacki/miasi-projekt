import { getAuth, clearAuth } from "../auth/auth-store";

const BASE_URL = "http://localhost:8080/api";

export async function apiClient(endpoint: string, options: RequestInit = {}) {
  const auth = getAuth();
  
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...((options.headers as Record<string, string>) || {}),
  };

  if (auth?.accessToken) {
    headers["Authorization"] = `Bearer ${auth.accessToken}`;
  }

  const response = await fetch(`${BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    if (response.status === 401) {
      clearAuth();
      window.location.href = "/login";
    }
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || "Wystąpił błąd podczas komunikacji z serwerem");
  }

  const text = await response.text();
  if (!text) {
    return null;
  }
  
  try {
    return JSON.parse(text);
  } catch (e) {
    return text;
  }
}
