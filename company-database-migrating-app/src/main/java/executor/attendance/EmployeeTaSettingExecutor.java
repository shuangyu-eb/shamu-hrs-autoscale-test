package executor.attendance;

import entity.attendance.EmployeeTaSetting;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EmployeeTaSettingExecutor extends BaseExecutor<EmployeeTaSetting> {

  private EmployeeTaSettingExecutor(final String companyId) {
    super(companyId, "employees_ta_settings");
  }

  public static EmployeeTaSettingExecutor getInstance(final String companyId) {
    return new EmployeeTaSettingExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String sql =
        "SELECT ets.* FROM %s ets "
            + "LEFT JOIN users u ON u.id = ets.employee_id "
            + "WHERE u.company_id = unhex(?)";
    return String.format(sql, tableName);
  }

  @Override
  protected EmployeeTaSetting buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final EmployeeTaSetting employeeTaSetting = new EmployeeTaSetting();
    employeeTaSetting.setEmployeeId(resultSet.getBinaryStream("employee_id"));
    employeeTaSetting.setMessagingOn(resultSet.getInt("messaging_on"));
    return employeeTaSetting;
  }

  @Override
  protected String getInsertSql() {
    final String sql =
        "INSERT INTO %s (id, employee_id, "
            + "messaging_on, created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, EmployeeTaSetting entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getEmployeeId());
    statement.setInt(3, entity.getMessagingOn());
    statement.setTimestamp(4, entity.getCreatedAt());
    statement.setTimestamp(5, entity.getUpdatedAt());
  }
}
