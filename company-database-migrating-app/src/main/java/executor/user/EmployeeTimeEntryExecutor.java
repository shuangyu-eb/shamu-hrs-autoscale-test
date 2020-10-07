package executor.user;

import entity.user.EmployeeTimeEntry;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EmployeeTimeEntryExecutor extends BaseExecutor<EmployeeTimeEntry> {

  private EmployeeTimeEntryExecutor(final String companyId) {
    super(companyId, "employee_time_entries");
  }

  public static EmployeeTimeEntryExecutor getInstance(final String companyId) {
    return new EmployeeTimeEntryExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String sql =
        "SELECT ete.* FROM %s ete "
            + "LEFT JOIN users u ON u.id = ete.employee_id "
            + "WHERE u.company_id = unhex(?)";
    return String.format(sql, tableName);
  }

  @Override
  protected EmployeeTimeEntry buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final EmployeeTimeEntry employeeTimeEntry = new EmployeeTimeEntry();
    employeeTimeEntry.setComment(resultSet.getString("comment"));
    employeeTimeEntry.setEmployeeId(resultSet.getBinaryStream("employee_id"));
    return employeeTimeEntry;
  }

  @Override
  protected String getInsertSql() {
    final String sql =
        "INSERT INTO %s (id, comment, employee_id, "
            + "created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, EmployeeTimeEntry entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setString(2, entity.getComment());
    statement.setBinaryStream(3, entity.getEmployeeId());
    statement.setTimestamp(4, entity.getCreatedAt());
    statement.setTimestamp(5, entity.getUpdatedAt());
  }
}
