package migrate.auth0;

import static migrate.util.PropertiesUtils.getProperty;

import com.auth0.client.auth.AuthAPI;
import java.io.IOException;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Auth0Config {

  private String jwks;

  private String domain;

  private String issuer;

  private String clientId;

  private String clientSecret;

  private String managementIdentifier;


  public Auth0Config() throws IOException {
    setJwks(getProperty("jwks"));
    setDomain(getProperty("domain"));
    setIssuer(getProperty("issuer"));
    setClientId(getProperty("clientId"));
    setClientSecret(getProperty("clientSecret"));
    setManagementIdentifier(getProperty("managementIdentifier"));
  }


  public AuthAPI getAuthApi() {
    return new AuthAPI(domain, clientId, clientSecret);
  }
}
