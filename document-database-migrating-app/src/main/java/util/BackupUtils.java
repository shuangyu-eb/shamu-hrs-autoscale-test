package util;

import static datasource.ConnectionManager.JDBC_DRIVER;

import com.smattme.MysqlExportService;
import datasource.ConnectionManager;
import java.sql.Connection;
import java.util.Properties;

public interface BackupUtils {

  static void dump() throws Exception {
    if(ExecuteLogUtils.isDumped()){
      return;
    }

    try {
      String documentDatabaseUrl = PropertiesUtils.getProperty("document_database_url");
      String documentDatabaseUser = PropertiesUtils.getProperty("document_database_user");
      String documentDatabasePassword = PropertiesUtils.getProperty("document_database_password");
      String dumpDir = "dump";

      Connection connection = ConnectionManager.getDocumentConnection();
      String databaseName = connection.getCatalog();
      Properties properties = new Properties();
      properties.setProperty(MysqlExportService.JDBC_CONNECTION_STRING, documentDatabaseUrl);
      properties.setProperty(MysqlExportService.DB_NAME, databaseName);
      properties.setProperty(MysqlExportService.DB_USERNAME, documentDatabaseUser);
      properties.setProperty(MysqlExportService.DB_PASSWORD, documentDatabasePassword);
      properties.setProperty(MysqlExportService.JDBC_DRIVER_NAME, JDBC_DRIVER);

      properties.setProperty(MysqlExportService.TEMP_DIR,
          DictionaryUtils.createDirIfNotExists(dumpDir).getPath());

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
