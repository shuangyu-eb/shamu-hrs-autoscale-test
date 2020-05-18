package datasource;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;
import util.PropertiesUtils;

public abstract class LiquibaseManager {

  private LiquibaseManager() {

  }

  public static void initDatabaseSchema(String schemaName) throws Exception {
    String templateUrl = getTenantDatabaseUrl(schemaName);
    String username = PropertiesUtils.getProperty("tenant_database_user");
    String password = PropertiesUtils.getProperty("tenant_database_password");

    String path = LiquibaseManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    String rootPath = URLDecoder.decode(path, "UTF-8");
    ResourceAccessor resourceAccessor = new FileSystemResourceAccessor(new File(rootPath));
    Database database = DatabaseFactory.getInstance().openDatabase(templateUrl, username, password, null, resourceAccessor);

    String changeLogPath = "db" + File.separator
        + "changelog" + File.separator
        + "schemas" + File.separator
        + "tenants" + File.separator
        + "hris-tenants-0.1.yml";
    DatabaseChangeLog databaseChangeLog = ChangeLogParserFactory.getInstance()
        .getParser("hris-tenants-0.1.yml", resourceAccessor)
        .parse(changeLogPath, new ChangeLogParameters(database), resourceAccessor);

    try(Liquibase liquibase = new Liquibase(databaseChangeLog, resourceAccessor, database)) {
      liquibase.clearCheckSums();
      liquibase.update(new Contexts());
    }
  }

  private static String getTenantDatabaseUrl(String schemaName) throws IOException {
    String url = PropertiesUtils.getProperty("tenant_database_url").replace("{schema_name}", schemaName);
    return url.contains("?")
        ? url.concat("&createDatabaseIfNotExist=true&jdbcCompliantTruncation=false")
        : url.concat("?createDatabaseIfNotExist=true&jdbcCompliantTruncation=false");
  }

  public static void improveDatabaseChangeLogTable(String schemaName) throws Exception {
    Connection connection = ConnectionManager.getTenantConnection();
    String tableName = schemaName +".DATABASECHANGELOG";
    String sql = "SELECT ID FROM "+ tableName;
    List<entity.DatabaseChangeLog> entities = new ArrayList<>();
    try(Statement statement = connection.createStatement()) {
      try(ResultSet resultSet = statement.executeQuery(sql)) {
        while (resultSet.next()) {
          entity.DatabaseChangeLog databaseChangeLog = new entity.DatabaseChangeLog();
          databaseChangeLog.setFileName("classpath:/db/changelog/schemas/tenants/hris-tenants-0.1.yml");
          databaseChangeLog.setId(resultSet.getString("ID"));
          entities.add(databaseChangeLog);
        }
      }
    }

    String selectSQL = "UPDATE " + tableName + " SET FILENAME = ? WHERE ID = ?";

    try(PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
      for(entity.DatabaseChangeLog entity : entities) {
        preparedStatement.setString(1, entity.getFileName());
        preparedStatement.setString(2, entity.getId());
        preparedStatement.executeUpdate();
      }
    }
  }
}
