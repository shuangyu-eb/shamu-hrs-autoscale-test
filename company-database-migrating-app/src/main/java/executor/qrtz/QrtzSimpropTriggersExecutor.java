package executor.qrtz;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QrtzSimpropTriggersExecutor extends QrtzBaseExecutor {

  private QrtzSimpropTriggersExecutor() {
    super("QRTZ_SIMPROP_TRIGGERS");
  }

  public static QrtzSimpropTriggersExecutor getInstance() {
    return new QrtzSimpropTriggersExecutor();
  }

  @Override
  protected String getInsertSql() {
    return "INSERT INTO %s VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, ResultSet resultSet)
      throws Exception {
    statement.setString(1, resultSet.getString("SCHED_NAME"));
    statement.setString(2, resultSet.getString("TRIGGER_NAME"));
    statement.setString(3, resultSet.getString("TRIGGER_GROUP"));
    statement.setString(4, resultSet.getString("STR_PROP_1"));
    statement.setString(5, resultSet.getString("STR_PROP_2"));
    statement.setString(6, resultSet.getString("STR_PROP_3"));
    statement.setInt(7, resultSet.getInt("INT_PROP_1"));
    statement.setInt(8, resultSet.getInt("INT_PROP_2"));
    statement.setBigDecimal(9, resultSet.getBigDecimal("DEC_PROP_1"));
    statement.setBigDecimal(10, resultSet.getBigDecimal("DEC_PROP_2"));
    statement.setString(11, resultSet.getString("BOOL_PROP_1"));
    statement.setString(12, resultSet.getString("BOOL_PROP_2"));
  }
}
