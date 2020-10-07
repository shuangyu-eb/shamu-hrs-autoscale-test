package manager;

import database.DataSource;
import database.DataSourceConfig;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import util.ExecuteLogUtils;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class LiquibaseManager {

  private LiquibaseManager() {}

  public static void initDefaultDatabaseSchema() throws Exception {
    // Schema company_default already created
    if (ExecuteLogUtils.companyRecorded(DataSourceConfig.getDefaultSchema())
        && ExecuteLogUtils.companyExecuted(DataSourceConfig.getDefaultSchema())) {
      log.info("Schema {} has already been created", DataSourceConfig.getDefaultSchema());
      return;
    }
    final String schema = DataSourceConfig.getDefaultSchema();
    final String url = getDatabaseUrl(schema);
    final String username = DataSourceConfig.getTargetUserName();
    final String password = DataSourceConfig.getTargetPassword();

    initLiquibase(url, username, password, "db.default.changelog-master.yml");
    improveDatabaseChangeLogTable(schema);
    // Schema company_default creates successfully
    ExecuteLogUtils.recordCompany(
        DataSourceConfig.getDefaultSchema(), ExecuteLogUtils.ExecuteStatus.SUCCESS);
  }

  public static void initTenantDatabaseSchema(final String companyId) throws Exception {
    final String schemaPrefix = DataSourceConfig.getTenantPrefix();
    final String schema = schemaPrefix + companyId;
    final String url = getDatabaseUrl(schema);
    final String username = DataSourceConfig.getTargetUserName();
    final String password = DataSourceConfig.getTargetPassword();

    initLiquibase(url, username, password, "db.tenant.changelog-master.yml");
    improveDatabaseChangeLogTable(schema);
  }

  private static void initLiquibase(
      final String url,
      final String username,
      final String password,
      final String changeLogFileName)
      throws Exception {

    final ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
    final Database database =
        DatabaseFactory.getInstance().openDatabase(url, username, password, null, resourceAccessor);
    final String changeLogPath =
        "db" + File.separator + "changelog" + File.separator + changeLogFileName;
    final DatabaseChangeLog databaseChangeLog =
        ChangeLogParserFactory.getInstance()
            .getParser(changeLogFileName, resourceAccessor)
            .parse(changeLogPath, new ChangeLogParameters(database), resourceAccessor);
    final Liquibase liquibase = new Liquibase(databaseChangeLog, resourceAccessor, database);
    liquibase.clearCheckSums();
    liquibase.update(new Contexts());
  }

  private static String getDatabaseUrl(final String schema) {
    final String url = DataSourceConfig.getTargetUrlTemplate().replace("{schema}", schema);
    return url.contains("?")
        ? url.concat("&createDatabaseIfNotExist=true&jdbcCompliantTruncation=false&useSSL=false")
        : url.concat("?createDatabaseIfNotExist=true&jdbcCompliantTruncation=false&useSSL=false");
  }

  private static void improveDatabaseChangeLogTable(final String schema) throws Exception {
    final String tableName = schema + ".DATABASECHANGELOG";
    final String sql = "SELECT ID, FILENAME FROM " + tableName;
    final List<entity.DatabaseChangeLog> entities = new ArrayList<>();
    final String selectSQL = "UPDATE " + tableName + " SET FILENAME = ? WHERE ID = ?";
    try (final Connection connection = DataSource.getTargetConnection();
        final Statement statement = connection.createStatement()) {
      try (final ResultSet resultSet = statement.executeQuery(sql)) {
        while (resultSet.next()) {
          final entity.DatabaseChangeLog databaseChangeLog = new entity.DatabaseChangeLog();
          final String fileName = resultSet.getString("FILENAME");
          final String filePath = String.join("/", fileName.split(File.separator));
          String realPath = filePath;
          if (schema.equals(DataSourceConfig.getDefaultSchema())) {
            realPath = "classpath:/" + filePath;
          }
          databaseChangeLog.setFileName(realPath);
          databaseChangeLog.setId(resultSet.getString("ID"));
          entities.add(databaseChangeLog);
        }
      }
    }

    try (final Connection connection = DataSource.getTargetConnection();
        final PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
      for (final entity.DatabaseChangeLog entity : entities) {
        preparedStatement.setString(1, entity.getFileName());
        preparedStatement.setString(2, entity.getId());
        preparedStatement.executeUpdate();
      }
    }
  }
}
