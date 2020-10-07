package executor.user;

import entity.user.EmployeeTimeLog;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EmployeeTimeLogExecutor extends BaseExecutor<EmployeeTimeLog> {

  private EmployeeTimeLogExecutor(final String companyId) {
    super(companyId, "employee_time_logs");
  }

  public static EmployeeTimeLogExecutor getInstance(final String companyId) {
    return new EmployeeTimeLogExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String sql =
        "SELECT etl.* FROM %s etl "
            + "WHERE etl.entry_id IN (SELECT ete.id FROM employee_time_entries ete "
            + "WHERE ete.employee_id IN (SELECT u.id FROM users u WHERE u.company_id = unhex(?)))";
    return String.format(sql, tableName);
  }

  @Override
  protected EmployeeTimeLog buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final EmployeeTimeLog timeLog = new EmployeeTimeLog();
    timeLog.setStart(resultSet.getTimestamp("start"));
    timeLog.setDurationMin(resultSet.getInt("duration_min"));
    timeLog.setTimeTypeId(resultSet.getBinaryStream("time_type_id"));
    timeLog.setEntryId(resultSet.getBinaryStream("entry_id"));
    return timeLog;
  }

  @Override
  protected String getInsertSql() {
    final String sql =
        "INSERT INTO %s (id, start, duration_min, "
            + "time_type_id, entry_id, created_at, "
            + "updated_at) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, EmployeeTimeLog entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setTimestamp(2, entity.getStart());
    statement.setInt(3, entity.getDurationMin());
    statement.setBinaryStream(4, getRelatedForeignKeyByRefId("static_employees_ta_time_types", entity.getTimeTypeId()));
    statement.setBinaryStream(5, entity.getEntryId());
    statement.setTimestamp(6, entity.getCreatedAt());
    statement.setTimestamp(7, entity.getUpdatedAt());
  }
}
