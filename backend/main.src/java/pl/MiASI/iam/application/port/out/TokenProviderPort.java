package pl.MiASI.iam.application.port.out;
import pl.MiASI.iam.domain.model.Account;
public interface TokenProviderPort { String generateToken(Account account); }