import { client } from "@/client/client.gen";
import { clearAuth, getAuth } from "@/shared/auth/auth-store";

function toApiError(error: unknown): Error {
  if (error instanceof Error) return error;
  if (
    error &&
    typeof error === "object" &&
    "message" in error &&
    typeof error.message === "string"
  ) {
    return new Error(error.message);
  }
  return new Error("Wystąpił błąd podczas komunikacji z serwerem");
}

export function configureApiClient(): void {
  client.interceptors.request.use((request) => {
    const auth = getAuth();
    if (auth?.accessToken) {
      request.headers.set("Authorization", `Bearer ${auth.accessToken}`);
    }
    return request;
  });

  client.interceptors.response.use((response) => {
    if (response.status === 401 && !response.url.includes("/api/auth/login")) {
      clearAuth();
      window.location.href = "/login";
    }
    return response;
  });

  client.interceptors.error.use((error) => toApiError(error));
}
