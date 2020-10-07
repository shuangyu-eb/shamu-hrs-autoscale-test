package executor.qrtz;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QrtzBlobTriggersExecutor extends QrtzBaseExecutor {
  private QrtzBlobTriggersExecutor() {
    super("QRTZ_BLOB_TRIGGERS");
  }

  public static QrtzBlobTriggersExecutor getInstance() {
    return new QrtzBlobTriggersExecutor();
  }

  @Override
  protected String getInsertSql() {
    return "INSERT INTO %s VALUES(?, ?, ?, ?)";
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, ResultSet resultSet)
      throws Exception {
    statement.setString(1, resultSet.getString("SCHED_NAME"));
    statement.setString(2, resultSet.getString("TRIGGER_NAME"));
    statement.setString(3, resultSet.getString("TRIGGER_GROUP"));
    statement.setBlob(4, resultSet.getBlob("BLOB_DATA"));
  }
}
