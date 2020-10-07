package executor.qrtz;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QrtzJobDetailsExecutor extends QrtzBaseExecutor {

  private QrtzJobDetailsExecutor() {
    super("QRTZ_JOB_DETAILS");
  }

  public static QrtzJobDetailsExecutor getInstance() {
    return new QrtzJobDetailsExecutor();
  }

  @Override
  protected String getInsertSql() {
    return "INSERT INTO %s VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, ResultSet resultSet)
      throws Exception {
    statement.setString(1, resultSet.getString("SCHED_NAME"));
    statement.setString(2, resultSet.getString("JOB_NAME"));
    statement.setString(3, resultSet.getString("JOB_GROUP"));
    statement.setString(4, resultSet.getString("DESCRIPTION"));
    statement.setString(5, resultSet.getString("JOB_CLASS_NAME"));
    statement.setString(6, resultSet.getString("IS_DURABLE"));
    statement.setString(7, resultSet.getString("IS_NONCONCURRENT"));
    statement.setString(8, resultSet.getString("IS_UPDATE_DATA"));
    statement.setString(9, resultSet.getString("REQUESTS_RECOVERY"));
    statement.setBlob(10, resultSet.getBlob("JOB_DATA"));
  }
}
