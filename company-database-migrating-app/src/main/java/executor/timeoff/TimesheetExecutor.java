package executor.timeoff;

import entity.timeoff.Timesheet;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TimesheetExecutor extends BaseExecutor<Timesheet> {

  private TimesheetExecutor(final String companyId) {
    super(companyId, "timesheets");
  }

  public static TimesheetExecutor getInstance(final String companyId) {
    return new TimesheetExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String sql =
        "SELECT t.* FROM %s t "
            + "WHERE t.employee_id IN ("
            + "SELECT u.id FROM users u WHERE u.company_id = unhex(?))";
    return String.format(sql, tableName);
  }

  @Override
  protected Timesheet buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final Timesheet timesheet = new Timesheet();
    timesheet.setEmployeeId(resultSet.getBinaryStream("employee_id"));
    timesheet.setStatusId(resultSet.getBinaryStream("status_id"));
    timesheet.setApproverEmployeeId(resultSet.getBinaryStream("approver_employee_id"));
    timesheet.setApprovedTimestamp(resultSet.getTimestamp("approved_timestamp"));
    timesheet.setUserCompensationId(resultSet.getBinaryStream("user_compensation_id"));
    timesheet.setTimePeriodId(resultSet.getBinaryStream("time_period_id"));
    timesheet.setRemovedAt(resultSet.getTimestamp("removed_at"));
    return timesheet;
  }

  @Override
  protected String getInsertSql() {
    final String sql =
        "INSERT INTO %s (id, employee_id, status_id, "
            + "approver_employee_id, approved_timestamp, user_compensation_id, "
            + "created_at, updated_at, time_period_id, "
            + "removed_at) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, Timesheet entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getEmployeeId());
    statement.setBinaryStream(
        3, getRelatedForeignKeyByName("static_timesheet_status", entity.getStatusId()));
    statement.setBinaryStream(4, entity.getApproverEmployeeId());
    statement.setTimestamp(5, entity.getApprovedTimestamp());
    statement.setBinaryStream(6, entity.getUserCompensationId());
    statement.setTimestamp(7, entity.getCreatedAt());
    statement.setTimestamp(8, entity.getUpdatedAt());
    statement.setBinaryStream(9, entity.getTimePeriodId());
    statement.setTimestamp(10, entity.getRemovedAt());
  }
}
