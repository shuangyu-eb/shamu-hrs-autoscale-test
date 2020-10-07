package database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.util.Properties;

public class DataSource {

  private static HikariDataSource targetDataSource;
  private static HikariDataSource originDataSource;

  static {
    buildTargetDataSource();
    buildOriginDataSource();
  }

  private DataSource() {}

  private static void buildTargetDataSource() {
    final String userName = DataSourceConfig.getTargetUserName();
    final String password = DataSourceConfig.getTargetPassword();
    final String url = DataSourceConfig.getTargetUrlTemplate().replace("{schema}", "");
    targetDataSource = new HikariDataSource(getHikariConfig(url, userName, password));
  }

  private static void buildOriginDataSource() {
    final String userName = DataSourceConfig.getOriginUserName();
    final String password = DataSourceConfig.getOriginPassword();
    final String url = DataSourceConfig.getOriginUrl();
    originDataSource = new HikariDataSource(getHikariConfig(url, userName, password));
  }

  public static Connection getTargetConnection() throws Exception {
    return targetDataSource.getConnection();
  }

  public static Connection getOriginConnection() throws Exception {
    return originDataSource.getConnection();
  }

  private static HikariConfig getHikariConfig(
      final String url, final String userName, final String password) {
    final HikariConfig config = new HikariConfig();
    config.setUsername(userName);
    config.setPassword(password);
    config.setJdbcUrl(url);
    config.setMaximumPoolSize(4);
    config.setConnectionTimeout(600000);
    config.setMinimumIdle(10);
    config.setMaxLifetime(1800000);
    config.setIdleTimeout(60000);
    config.setValidationTimeout(3000);
    final Properties prop = new Properties();
    prop.setProperty("jdbcCompliantTruncation", Boolean.FALSE.toString());
    prop.setProperty("createDatabaseIfNotExist", Boolean.TRUE.toString());
    prop.setProperty("userSSL", Boolean.FALSE.toString());
    config.setDataSourceProperties(prop);
    return config;
  }

  public static void closeDataSource() {
    originDataSource.close();
    targetDataSource.close();
  }
}
