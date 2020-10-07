package executor.qrtz;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QrtzLocksExecutor extends QrtzBaseExecutor {

  private QrtzLocksExecutor() {
    super("QRTZ_LOCKS");
  }

  public static QrtzLocksExecutor getInstance() {
    return new QrtzLocksExecutor();
  }

  @Override
  protected String getInsertSql() {
    return "INSERT INTO %s VALUES(?, ?)";
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, ResultSet resultSet)
      throws Exception {
    statement.setString(1, resultSet.getString("SCHED_NAME"));
    statement.setString(2, resultSet.getString("LOCK_NAME"));
  }
}
