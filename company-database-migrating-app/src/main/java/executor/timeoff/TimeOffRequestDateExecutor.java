package executor.timeoff;

import entity.timeoff.TimeOffRequestDate;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TimeOffRequestDateExecutor extends BaseExecutor<TimeOffRequestDate> {

  private TimeOffRequestDateExecutor(final String companyId) {
    super(companyId, "time_off_request_dates");
  }

  public static TimeOffRequestDateExecutor getInstance(final String companyId) {
    return new TimeOffRequestDateExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT trd.* FROM %s trd "
            + "LEFT JOIN time_off_requests tr ON tr.id = trd.time_off_request_id "
            + "LEFT JOIN users u ON tr.requester_user_id = u.id "
            + "WHERE u.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected TimeOffRequestDate buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final TimeOffRequestDate timeOffRequestDate = new TimeOffRequestDate();
    timeOffRequestDate.setTimeOffRequestId(resultSet.getBinaryStream("time_off_request_id"));
    timeOffRequestDate.setDate(resultSet.getTimestamp("date"));
    timeOffRequestDate.setHours(resultSet.getInt("hours"));
    return timeOffRequestDate;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, time_off_request_id, "
            + "date, hours, created_at, "
            + "updated_at) "
            + "VALUES(?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, TimeOffRequestDate entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getTimeOffRequestId());
    statement.setTimestamp(3, entity.getDate());
    statement.setInt(4, entity.getHours());
    statement.setTimestamp(5, entity.getCreatedAt());
    statement.setTimestamp(6, entity.getUpdatedAt());
  }
}
