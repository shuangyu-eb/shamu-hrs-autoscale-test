package executor.timeoff;

import entity.timeoff.TimeOffPolicyAccrualScheduleMilestone;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TimeOffPolicyAccrualScheduleMilestoneExecutor
    extends BaseExecutor<TimeOffPolicyAccrualScheduleMilestone> {

  private TimeOffPolicyAccrualScheduleMilestoneExecutor(final String companyId) {
    super(companyId, "time_off_policy_accrual_schedule_milestones");
  }

  public static TimeOffPolicyAccrualScheduleMilestoneExecutor getInstance(final String companyId) {
    return new TimeOffPolicyAccrualScheduleMilestoneExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT tpasm.* FROM %s tpasm "
            + "LEFT JOIN time_off_policy_accrual_schedules tpas ON tpasm.time_off_policy_accrual_schedule_id = tpasm.id "
            + "LEFT JOIN time_off_policies tp ON tp.id = tpas.time_off_policy_id "
            + "WHERE tp.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected TimeOffPolicyAccrualScheduleMilestone buildEntityByResultSet(ResultSet resultSet)
      throws Exception {
    final TimeOffPolicyAccrualScheduleMilestone timeOffPolicyAccrualScheduleMilestone =
        new TimeOffPolicyAccrualScheduleMilestone();
    timeOffPolicyAccrualScheduleMilestone.setAnniversaryYear(resultSet.getInt("anniversary_year"));
    timeOffPolicyAccrualScheduleMilestone.setTimeOffPolicyAccrualScheduleId(
        resultSet.getBinaryStream("time_off_policy_accrual_schedule_id"));
    timeOffPolicyAccrualScheduleMilestone.setAccrualHours(resultSet.getInt("accrual_hours"));
    timeOffPolicyAccrualScheduleMilestone.setAccrualInterval(resultSet.getInt("accrual_interval"));
    timeOffPolicyAccrualScheduleMilestone.setCarryoverLimit(resultSet.getInt("carryover_limit"));
    timeOffPolicyAccrualScheduleMilestone.setMaxBalance(resultSet.getInt("max_balance"));
    timeOffPolicyAccrualScheduleMilestone.setName(resultSet.getString("name"));
    timeOffPolicyAccrualScheduleMilestone.setExpiredAt(resultSet.getTimestamp("expired_at"));
    return timeOffPolicyAccrualScheduleMilestone;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, anniversary_year, time_off_policy_accrual_schedule_id, "
            + "accrual_hours, accrual_interval, carryover_limit, "
            + "max_balance, name, created_at, "
            + "updated_at, expired_at) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(
      PreparedStatement statement, TimeOffPolicyAccrualScheduleMilestone entity) throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setInt(2, entity.getAnniversaryYear());
    statement.setBinaryStream(3, entity.getTimeOffPolicyAccrualScheduleId());
    statement.setInt(4, entity.getAccrualHours());
    statement.setInt(5, entity.getAccrualInterval());
    statement.setInt(6, entity.getCarryoverLimit());
    statement.setInt(7, entity.getMaxBalance());
    statement.setString(8, entity.getName());
    statement.setTimestamp(9, entity.getCreatedAt());
    statement.setTimestamp(10, entity.getUpdatedAt());
    statement.setTimestamp(11, entity.getExpiredAt());
  }
}
