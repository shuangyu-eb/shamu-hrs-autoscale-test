package executor.qrtz;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QrtzCronTriggersExecutor extends QrtzBaseExecutor {
  private QrtzCronTriggersExecutor() {
    super("QRTZ_CRON_TRIGGERS");
  }

  public static QrtzCronTriggersExecutor getInstance() {
    return new QrtzCronTriggersExecutor();
  }

  @Override
  protected String getInsertSql() {
    return "INSERT INTO %s VALUES(?, ?, ?, ?, ?)";
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, ResultSet resultSet)
      throws Exception {
    statement.setString(1, resultSet.getString("SCHED_NAME"));
    statement.setString(2, resultSet.getString("TRIGGER_NAME"));
    statement.setString(3, resultSet.getString("TRIGGER_GROUP"));
    statement.setString(4, resultSet.getString("CRON_EXPRESSION"));
    statement.setString(5, resultSet.getString("TIME_ZONE_ID"));
  }
}
