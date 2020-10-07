package executor.qrtz;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QrtzCalendarsExecutor extends QrtzBaseExecutor {
  private QrtzCalendarsExecutor() {
    super("QRTZ_CALENDARS");
  }

  public static QrtzCalendarsExecutor getInstance() {
    return new QrtzCalendarsExecutor();
  }

  @Override
  protected String getInsertSql() {
    return "INSERT INTO %s VALUES(?, ?, ?)";
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, ResultSet resultSet)
      throws Exception {
    statement.setString(1, resultSet.getString("SCHED_NAME"));
    statement.setString(2, resultSet.getString("CALENDAR_NAME"));
    statement.setBlob(3, resultSet.getBlob("CALENDAR"));
  }
}
