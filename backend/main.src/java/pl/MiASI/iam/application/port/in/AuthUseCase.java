package pl.MiASI.iam.application.port.in;
import pl.MiASI.iam.domain.model.AccountId;
import pl.MiASI.iam.domain.model.Role;
public interface AuthUseCase {
    AuthResult login(String email, String password);
    AccountId registerUser(String email, String password, Role role);
    void activateAccount(String token);
    void logout(String token);
    void updateEmail(AccountId accountId, String newEmail);
    void deactivateAccount(AccountId accountId);
    void updateRole(AccountId accountId, Role role);
}