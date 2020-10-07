package executor.timeoff;

import entity.timeoff.TimeOffAdjustment;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TimeOffAdjustmentExecutor extends BaseExecutor<TimeOffAdjustment> {

  private TimeOffAdjustmentExecutor(final String companyId) {
    super(companyId, "time_off_adjustments");
  }

  public static TimeOffAdjustmentExecutor getInstance(final String companyId) {
    return new TimeOffAdjustmentExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String queryId =
        "SELECT * FROM %s ta "
            + "LEFT JOIN time_off_policies tp ON ta.time_off_policy_id = tp.id "
            + "WHERE tp.company_id = unhex(?)";
    return String.format(queryId, tableName);
  }

  @Override
  protected TimeOffAdjustment buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final TimeOffAdjustment timeOffAdjustment = new TimeOffAdjustment();
    timeOffAdjustment.setUserId(resultSet.getBinaryStream("user_id"));
    timeOffAdjustment.setAdjusterUserId(resultSet.getBinaryStream("adjuster_user_id"));
    timeOffAdjustment.setAmount(resultSet.getInt("amount"));
    timeOffAdjustment.setTimeOffPolicyId(resultSet.getBinaryStream("time_off_policy_id"));
    timeOffAdjustment.setComment(resultSet.getString("comment"));
    return timeOffAdjustment;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, user_id, adjuster_user_id, "
            + "amount, time_off_policy_id, created_at, "
            + "comment, updated_at) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, TimeOffAdjustment entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getUserId());
    statement.setBinaryStream(3, entity.getAdjusterUserId());
    statement.setInt(4, entity.getAmount());
    statement.setBinaryStream(5, entity.getTimeOffPolicyId());
    statement.setTimestamp(6, entity.getCreatedAt());
    statement.setString(7, entity.getComment());
    statement.setTimestamp(8, entity.getUpdatedAt());
  }
}
