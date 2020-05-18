package datasource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.PropertiesUtils;

public abstract class ConnectionManager {

  public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

  private static Connection companyConnection;

  private static Connection documentConnection;

  private static Connection tenantConnection;

  private static final List<Connection> connections = new ArrayList<>();

  private ConnectionManager() {

  }

  public static Connection getCompanyConnection () throws Exception {
    if (companyConnection == null) {
      String companyDatabaseUrl = PropertiesUtils.getProperty("company_database_url");
      String companyDatabaseUser = PropertiesUtils.getProperty("company_database_user");
      String companyDatabasePassword = PropertiesUtils.getProperty("company_database_password");
      companyConnection = ConnectionManager
          .getConnection(companyDatabaseUrl, companyDatabaseUser, companyDatabasePassword);
    }
    return companyConnection;
  }

  public static Connection getDocumentConnection () throws Exception {
    if(documentConnection == null) {
      String documentDatabaseUrl = PropertiesUtils.getProperty("document_database_url");
      String documentDatabaseUser = PropertiesUtils.getProperty("document_database_user");
      String documentDatabasePassword = PropertiesUtils.getProperty("document_database_password");
      documentConnection = ConnectionManager
          .getConnection(documentDatabaseUrl, documentDatabaseUser ,documentDatabasePassword);
    }
    return documentConnection;
  }

  public static Connection getTenantConnection () throws Exception {
    if(tenantConnection == null) {
      String tenantDatabaseUrl = getTenantDatabaseUrl();
      String tenantDatabaseUser = PropertiesUtils.getProperty("tenant_database_user");
      String tenantDatabasePassword = PropertiesUtils.getProperty("tenant_database_password");
      tenantConnection = ConnectionManager
          .getConnection(tenantDatabaseUrl, tenantDatabaseUser ,tenantDatabasePassword);
      tenantConnection.setAutoCommit(false);
    }
    return tenantConnection;
  }

  private static Connection getConnection(String url, String user, String password) throws Exception {
    Class.forName(JDBC_DRIVER);
    Connection connection = DriverManager.getConnection(url, user, password);
    connection.setAutoCommit(false);
    connections.add(connection);
    return connection;
  }

  private static String getTenantDatabaseUrl() throws IOException {
    String url = PropertiesUtils.getProperty("tenant_database_url").replace("{schema_name}", "");
    return url.contains("?")
        ? url.concat("&createDatabaseIfNotExist=true")
        : url.concat("?createDatabaseIfNotExist=true");
  }

  public static void resetAllConnections() {
    close();
    documentConnection = null;
    companyConnection = null;
    tenantConnection = null;
  }

  public static void commit() {
    try {
      if (tenantConnection != null) {
        tenantConnection.commit();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void close() {
    connections.forEach(connection -> {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
    connections.clear();
  }

}
