package migrate.datasource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import migrate.util.PropertiesUtils;

public class ConnectionManager {

  private static Connection connection;

  public static Connection get() throws IOException, SQLException {
    if (connection == null) {
      String url = PropertiesUtils.getProperty("url");
      String user = PropertiesUtils.getProperty("user");
      String password = PropertiesUtils.getProperty("password");

      connection = DriverManager.getConnection(url, user, password);
    }
    return connection;
  }

  public static void reset() {
    connection = null;
  }
}
