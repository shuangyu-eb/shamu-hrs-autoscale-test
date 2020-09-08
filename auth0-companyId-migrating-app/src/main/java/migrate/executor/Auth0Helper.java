package migrate.executor;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.APIException;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.auth0.json.mgmt.users.UsersPage;
import com.auth0.net.Request;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import migrate.auth0.Auth0Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Auth0Helper {

  private final Auth0Manager auth0Manager;

  @Autowired
  public Auth0Helper(final Auth0Manager auth0Manager) {
    this.auth0Manager = auth0Manager;
  }


  public User getUserByUserIdFromAuth0(final String userId) {
    final ManagementAPI manager = auth0Manager.getManagementApi();
    UserFilter userFilter = new UserFilter();
    userFilter =
        userFilter
            .withSearchEngine("v3")
            .withQuery(String.format("app_metadata.id:\"%s\"", userId.toLowerCase()));
    final Request<UsersPage> userRequest = manager.users().list(userFilter);
    final UsersPage usersPage;
    try {
      usersPage = userRequest.execute();
    } catch (Auth0Exception e) {
      log.error("User Request execute failed", e);
      throw new RuntimeException("No user with id: " + userId);
    }
    final List<User> users = usersPage.getItems();
    if (users.isEmpty()) {
      throw new RuntimeException("No user with id: " + userId);
    }
    return users.get(0);
  }

  public void updateCompanyId(migrate.entity.User user) {
    final ManagementAPI manager = auth0Manager.getManagementApi();

    User auth0User = getUserByUserIdFromAuth0(user.getId());
    Map<String, Object> appMetaData = auth0User.getAppMetadata();
    appMetaData.put("companyId", user.getCompanyId());

    User updateUser = new User();
    updateUser.setAppMetadata(appMetaData);
    final Request<User> request =
        manager.users().update(auth0User.getId(), updateUser);
    try {
      request.execute();
    } catch (APIException e) {
      log.error("Request was executed but the response wasn't successful", e);
    } catch (Auth0Exception e) {
      log.error("Request execute failed.", e);
    }
  }


}
