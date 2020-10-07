package executor.timeoff;

import entity.timeoff.TimeOffPolicy;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TimeOffPolicyExecutor extends BaseExecutor<TimeOffPolicy> {

  private TimeOffPolicyExecutor(final String companyId) {
    super(companyId, "time_off_policies");
  }

  public static TimeOffPolicyExecutor getInstance(final String companyId) {
    return new TimeOffPolicyExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql = "SELECT * FROM %s " + "WHERE company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected TimeOffPolicy buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final TimeOffPolicy timeOffPolicy = new TimeOffPolicy();
    timeOffPolicy.setIsLimited(resultSet.getBoolean("is_limited"));
    timeOffPolicy.setName(resultSet.getString("name"));
    timeOffPolicy.setIsAutoEnrollEnabled(resultSet.getBoolean("is_auto_enroll_enabled"));
    return timeOffPolicy;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, is_limited, created_at, "
            + "updated_at, name, is_auto_enroll_enabled) "
            + "VALUES(?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, TimeOffPolicy entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBoolean(2, entity.getIsLimited());
    statement.setTimestamp(3, entity.getCreatedAt());
    statement.setTimestamp(4, entity.getUpdatedAt());
    statement.setString(5, entity.getName());
    statement.setBoolean(6, entity.getIsAutoEnrollEnabled());
  }
}
