package executor.qrtz;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QrtzFiredTriggersExecutor extends QrtzBaseExecutor {

  private QrtzFiredTriggersExecutor() {
    super("QRTZ_FIRED_TRIGGERS");
  }

  public static QrtzFiredTriggersExecutor getInstance() {
    return new QrtzFiredTriggersExecutor();
  }

  @Override
  protected String getInsertSql() {
    return "INSERT INTO %s VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, ResultSet resultSet)
      throws Exception {
    statement.setString(1, resultSet.getString("SCHED_NAME"));
    statement.setString(2, resultSet.getString("ENTRY_ID"));
    statement.setString(3, resultSet.getString("TRIGGER_NAME"));
    statement.setString(4, resultSet.getString("TRIGGER_GROUP"));
    statement.setString(5, resultSet.getString("INSTANCE_NAME"));
    statement.setInt(6, resultSet.getInt("FIRED_TIME"));
    statement.setInt(7, resultSet.getInt("SCHED_TIME"));
    statement.setInt(8, resultSet.getInt("PRIORITY"));
    statement.setString(9, resultSet.getString("STATE"));
    statement.setString(10, resultSet.getString("JOB_NAME"));
    statement.setString(11, resultSet.getString("JOB_GROUP"));
    statement.setString(12, resultSet.getString("IS_NONCONCURRENT"));
    statement.setString(13, resultSet.getString("REQUESTS_RECOVERY"));
  }
}
