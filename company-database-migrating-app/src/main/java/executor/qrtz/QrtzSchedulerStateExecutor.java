package executor.qrtz;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QrtzSchedulerStateExecutor extends QrtzBaseExecutor {

  private QrtzSchedulerStateExecutor() {
    super("QRTZ_SCHEDULER_STATE");
  }

  public static QrtzSchedulerStateExecutor getInstance() {
    return new QrtzSchedulerStateExecutor();
  }

  @Override
  protected String getInsertSql() {
    return "INSERT INTO %s VALUES(?, ?, ?, ?)";
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, ResultSet resultSet)
      throws Exception {
    statement.setString(1, resultSet.getString("SCHED_NAME"));
    statement.setString(2, resultSet.getString("INSTANCE_NAME"));
    statement.setInt(3, resultSet.getInt("LAST_CHECKIN_TIME"));
    statement.setInt(4, resultSet.getInt("CHECKIN_INTERVAL"));
  }
}
