package executor.qrtz;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QrtzPausedTriggerGrpsExecutor extends QrtzBaseExecutor {

  private QrtzPausedTriggerGrpsExecutor() {
    super("QRTZ_PAUSED_TRIGGER_GRPS");
  }

  public static QrtzPausedTriggerGrpsExecutor getInstance() {
    return new QrtzPausedTriggerGrpsExecutor();
  }

  @Override
  protected String getInsertSql() {
    return "INSERT INTO %s VALUES(?, ?)";
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, ResultSet resultSet)
      throws Exception {
    statement.setString(1, resultSet.getString("SCHED_NAME"));
    statement.setString(2, resultSet.getString("TRIGGER_GROUP"));
  }
}
