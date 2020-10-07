package executor.qrtz;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QrtzTriggersExecutor extends QrtzBaseExecutor {

  private QrtzTriggersExecutor() {
    super("QRTZ_TRIGGERS");
  }

  public static QrtzTriggersExecutor getInstance() {
    return new QrtzTriggersExecutor();
  }

  @Override
  protected String getInsertSql() {
    return "INSERT INTO %s VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, ResultSet resultSet)
      throws Exception {
    statement.setString(1, resultSet.getString("SCHED_NAME"));
    statement.setString(2, resultSet.getString("TRIGGER_NAME"));
    statement.setString(3, resultSet.getString("TRIGGER_GROUP"));
    statement.setString(4, resultSet.getString("JOB_NAME"));
    statement.setString(5, resultSet.getString("JOB_GROUP"));
    statement.setString(6, resultSet.getString("DESCRIPTION"));
    statement.setInt(7, resultSet.getInt("NEXT_FIRE_TIME"));
    statement.setInt(8, resultSet.getInt("PREV_FIRE_TIME"));
    statement.setInt(9, resultSet.getInt("PRIORITY"));
    statement.setString(10, resultSet.getString("TRIGGER_STATE"));
    statement.setString(11, resultSet.getString("TRIGGER_TYPE"));
    statement.setInt(12, resultSet.getInt("START_TIME"));
    statement.setInt(13, resultSet.getInt("END_TIME"));
    statement.setString(14, resultSet.getString("CALENDAR_NAME"));
    statement.setInt(15, resultSet.getInt("MISFIRE_INSTR"));
    statement.setBlob(16, resultSet.getBlob("JOB_DATA"));
  }
}
