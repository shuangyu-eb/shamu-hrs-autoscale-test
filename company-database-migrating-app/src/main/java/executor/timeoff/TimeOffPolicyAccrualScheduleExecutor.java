package executor.timeoff;

import entity.timeoff.TimeOffPolicyAccrualSchedule;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TimeOffPolicyAccrualScheduleExecutor
    extends BaseExecutor<TimeOffPolicyAccrualSchedule> {

  private TimeOffPolicyAccrualScheduleExecutor(final String companyId) {
    super(companyId, "time_off_policy_accrual_schedules");
  }

  public static TimeOffPolicyAccrualScheduleExecutor getInstance(final String companyId) {
    return new TimeOffPolicyAccrualScheduleExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT tpas.* FROM %s tpas "
            + "LEFT JOIN time_off_policies tp ON tpas.time_off_policy_id = tp.id "
            + "WHERE tp.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected TimeOffPolicyAccrualSchedule buildEntityByResultSet(ResultSet resultSet)
      throws Exception {
    final TimeOffPolicyAccrualSchedule timeOffPolicyAccrualSchedule =
        new TimeOffPolicyAccrualSchedule();
    timeOffPolicyAccrualSchedule.setTimeOffPolicyId(
        resultSet.getBinaryStream("time_off_policy_id"));
    timeOffPolicyAccrualSchedule.setAccrualHours(resultSet.getInt("accrual_hours"));
    timeOffPolicyAccrualSchedule.setMaxBalance(resultSet.getInt("max_balance"));
    timeOffPolicyAccrualSchedule.setDaysBeforeAccrualStarts(
        resultSet.getInt("days_before_accrual_starts"));
    timeOffPolicyAccrualSchedule.setCarryoverLimit(resultSet.getInt("carryover_limit"));
    timeOffPolicyAccrualSchedule.setTimeOffAccrualFrequencyId(
        resultSet.getBinaryStream("time_off_accrual_frequency_id"));
    timeOffPolicyAccrualSchedule.setExpiredAt(resultSet.getTimestamp("expired_at"));
    return timeOffPolicyAccrualSchedule;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, time_off_policy_id, accrual_hours, "
            + "max_balance, created_at, updated_at, "
            + "days_before_accrual_starts, carryover_limit, time_off_accrual_frequency_id, "
            + "expired_at) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(
      PreparedStatement statement, TimeOffPolicyAccrualSchedule entity) throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getTimeOffPolicyId());
    statement.setInt(3, entity.getAccrualHours());
    statement.setInt(4, entity.getMaxBalance());
    statement.setTimestamp(5, entity.getCreatedAt());
    statement.setTimestamp(6, entity.getUpdatedAt());
    statement.setInt(7, entity.getDaysBeforeAccrualStarts());
    statement.setInt(8, entity.getCarryoverLimit());
    statement.setBinaryStream(
        9,
        getRelatedForeignKeyByRefId(
            "time_off_accrual_frequencies", entity.getTimeOffAccrualFrequencyId()));
    statement.setTimestamp(10, entity.getExpiredAt());
  }
}
