package migrate;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import migrate.entity.User;
import migrate.executor.Auth0Helper;
import migrate.executor.UserDataReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@Slf4j
public class Main {


  public static void main(String[] args) {
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
        Main.class);
    UserDataReader userDataReader = applicationContext.getBean(UserDataReader.class);
    Auth0Helper auth0Helper = applicationContext.getBean(Auth0Helper.class);

    List<User> users = userDataReader.getUsers();
    for (User user : users) {
      try {
        log.info("userId: {}, companyId: {}, start execute", user.getId(), user.getCompanyId());
        auth0Helper.updateCompanyId(user);
      } catch (RuntimeException e) {
        log.error(String.format("User with id: %s execute failed, companyId: %s", user.getId(),
            user.getCompanyId()), e);
      }

    }
  }

}
