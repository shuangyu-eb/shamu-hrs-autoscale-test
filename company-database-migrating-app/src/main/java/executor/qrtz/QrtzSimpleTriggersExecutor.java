package executor.qrtz;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QrtzSimpleTriggersExecutor extends QrtzBaseExecutor {

  private QrtzSimpleTriggersExecutor() {
    super("QRTZ_SIMPLE_TRIGGERS");
  }

  public static QrtzSimpleTriggersExecutor getInstance() {
    return new QrtzSimpleTriggersExecutor();
  }

  @Override
  protected String getInsertSql() {
    return "INSERT INTO %s VALUES(? ,?, ?, ?, ?, ?)";
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, ResultSet resultSet)
      throws Exception {
    statement.setString(1, resultSet.getString("SCHED_NAME"));
    statement.setString(2, resultSet.getString("TRIGGER_NAME"));
    statement.setString(3, resultSet.getString("TRIGGER_GROUP"));
    statement.setInt(4, resultSet.getInt("REPEAT_COUNT"));
    statement.setInt(5, resultSet.getInt("REPEAT_INTERVAL"));
    statement.setInt(6, resultSet.getInt("TIMES_TRIGGERED"));
  }
}
