package migrate.executor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import migrate.datasource.ConnectionManager;
import migrate.entity.User;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserDataReader {

  private List<User> users;

  public List<User> getUsers() {
    if (users == null) {
      String sql = "SELECT hex(id) AS id, hex(company_id) AS companyId from users";
      users = new ArrayList<>();
      try (Connection connection = ConnectionManager.get();
          PreparedStatement statement = connection.prepareStatement(sql)) {
        try (ResultSet resultSet = statement.executeQuery()) {
          while (resultSet.next()) {
            String id = resultSet.getString(1);
            String companyId = resultSet.getString(2);
            users.add(new User(id, companyId));
          }
        }
      } catch (SQLException | IOException e) {
        log.error("Get users from origin company-service database failed", e);
      }
    }

    return users;
  }
}
