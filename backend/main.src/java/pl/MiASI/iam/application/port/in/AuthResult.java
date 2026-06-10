package pl.MiASI.iam.application.port.in;
public record AuthResult(String userId, String email, String role, String accessToken) {}