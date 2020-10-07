package executor.timeoff;

import entity.timeoff.TimeOffRequest;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TimeOffRequestExecutor extends BaseExecutor<TimeOffRequest> {

  private TimeOffRequestExecutor(final String companyId) {
    super(companyId, "time_off_requests");
  }

  public static TimeOffRequestExecutor getInstance(final String companyId) {
    return new TimeOffRequestExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT tr.* FROM %s tr "
            + "LEFT JOIN users u ON u.id = tr.requester_user_id "
            + "WHERE u.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected TimeOffRequest buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final TimeOffRequest timeOffRequest = new TimeOffRequest();
    timeOffRequest.setRequesterUserId(resultSet.getBinaryStream("requester_user_id"));
    timeOffRequest.setApproverUserId(resultSet.getBinaryStream("approver_user_id"));
    timeOffRequest.setApprovedDate(resultSet.getTimestamp("approved_date"));
    timeOffRequest.setTimeOffPolicyId(resultSet.getBinaryStream("time_off_policy_id"));
    timeOffRequest.setExpiresAt(resultSet.getTimestamp("expires_at"));
    timeOffRequest.setTimeOffRequestApprovalStatusId(
        resultSet.getBinaryStream("time_off_request_approval_status_id"));
    timeOffRequest.setBalance(resultSet.getInt("balance"));
    return timeOffRequest;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, requester_user_id, approver_user_id, "
            + "approved_date, time_off_policy_id, expires_at, "
            + "created_at, updated_at, time_off_request_approval_status_id, "
            + "balance) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, TimeOffRequest entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getRequesterUserId());
    statement.setBinaryStream(3, entity.getApproverUserId());
    statement.setTimestamp(4, entity.getApprovedDate());
    statement.setBinaryStream(5, entity.getTimeOffPolicyId());
    statement.setTimestamp(6, entity.getExpiresAt());
    statement.setTimestamp(7, entity.getCreatedAt());
    statement.setTimestamp(8, entity.getUpdatedAt());
    statement.setBinaryStream(
        9,
        getRelatedForeignKeyByRefId(
            "time_off_request_approval_statuses", entity.getTimeOffRequestApprovalStatusId()));
    statement.setInt(10, entity.getBalance());
  }
}
