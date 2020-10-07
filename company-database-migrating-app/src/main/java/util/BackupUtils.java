package util;

import com.smattme.MysqlExportService;
import database.DataSource;
import java.sql.Connection;
import java.util.Properties;

public class BackupUtils {
  private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

  public static void dump() throws Exception {
    if (ExecuteLogUtils.isDumped()) {
      return;
    }

    try {
      String companyDatabaseUrl =
          PropertiesUtils.getProperty("origin.jdbc-url")
              + "/"
              + PropertiesUtils.getProperty("origin.schema");
      String companyDatabaseUser = PropertiesUtils.getProperty("origin.user");
      String companyDatabasePassword = PropertiesUtils.getProperty("origin.password");
      String dumpDir = "dump";

      Connection connection = DataSource.getOriginConnection();
      String databaseName = connection.getCatalog();
      Properties properties = new Properties();
      properties.setProperty(MysqlExportService.JDBC_CONNECTION_STRING, companyDatabaseUrl);
      properties.setProperty(MysqlExportService.DB_NAME, databaseName);
      properties.setProperty(MysqlExportService.DB_USERNAME, companyDatabaseUser);
      properties.setProperty(MysqlExportService.DB_PASSWORD, companyDatabasePassword);
      properties.setProperty(MysqlExportService.JDBC_DRIVER_NAME, JDBC_DRIVER);
      properties.setProperty(
          MysqlExportService.TEMP_DIR, DirectoryUtils.createDirIfNotExists(dumpDir).getPath());

      MysqlExportService mysqlExportService = new MysqlExportService(properties);
      mysqlExportService.clearTempFiles(false);
      properties.setProperty(MysqlExportService.PRESERVE_GENERATED_ZIP, "true");
      mysqlExportService.export();
      ExecuteLogUtils.markDumped(true);
    } catch (Exception e) {
      ExecuteLogUtils.markDumped(false);
      throw e;
    }
  }
}
