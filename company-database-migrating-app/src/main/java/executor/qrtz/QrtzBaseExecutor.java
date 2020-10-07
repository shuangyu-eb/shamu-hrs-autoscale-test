package executor.qrtz;

import database.DataSource;
import database.DataSourceConfig;
import lombok.extern.slf4j.Slf4j;
import util.ExecuteLogUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
public abstract class QrtzBaseExecutor {

  private final String originTableName;

  private final String targetTableName;

  private final String tableName;

  public QrtzBaseExecutor(final String tableName) {
    this.originTableName = DataSourceConfig.getOriginSchema() + "." + tableName;
    this.targetTableName = DataSourceConfig.getDefaultSchema() + "." + tableName;
    this.tableName = tableName;
  }

  public void execute() throws Exception {
    if (ExecuteLogUtils.companyRecorded(tableName) && ExecuteLogUtils.companyExecuted(tableName)) {
      log.info("Table {} has already been created", tableName);
      return;
    }

    log.info("Start execute, tableName: {}", tableName);
    ResultSet resultSet = null;

    try (final Connection originConnection = DataSource.getOriginConnection();
         final Connection targetConnection = DataSource.getTargetConnection();
      final Statement originConnectionStatement = originConnection.createStatement();
        final PreparedStatement targetConnectionStatement =
            targetConnection.prepareStatement(String.format(getInsertSql(), targetTableName))) {
      final String sql = String.format("SELECT * FROM %s", originTableName);
      resultSet = originConnectionStatement.executeQuery(sql);

      while (resultSet.next()) {
        buildInsertStatement(targetConnectionStatement, resultSet);
        targetConnectionStatement.execute();
      }
      log.info("{} execute completed", tableName);

      ExecuteLogUtils.recordCompany(tableName, ExecuteLogUtils.ExecuteStatus.SUCCESS);
    } catch (Exception e) {
      log.info(String.format("Failed to load data from table %s.", tableName));
      log.error(String.valueOf(e));
    } finally {
      if (resultSet != null) {
        // close resources
        resultSet.close();
      }
    }
  }

  protected abstract String getInsertSql();

  protected abstract void buildInsertStatement(PreparedStatement statement, ResultSet resultSet)
      throws Exception;
}
