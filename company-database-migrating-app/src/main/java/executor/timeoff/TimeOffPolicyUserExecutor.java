package executor.timeoff;

import lombok.extern.slf4j.Slf4j;
import org.zeroturnaround.zip.commons.IOUtils;

import entity.timeoff.TimeOffPolicyUser;
import executor.BaseExecutor;
import util.UuidUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

@Slf4j
public class TimeOffPolicyUserExecutor extends BaseExecutor<TimeOffPolicyUser> {

  private TimeOffPolicyUserExecutor(final String companyId) {
    super(companyId, "time_off_policies_users");
  }

  public static TimeOffPolicyUserExecutor getInstance(final String companyId) {
    return new TimeOffPolicyUserExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT tpu.* FROM %s tpu "
            + "LEFT JOIN time_off_policies tp ON tpu.time_off_policy_id = tp.id "
            + "WHERE tp.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected TimeOffPolicyUser buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final TimeOffPolicyUser timeOffPolicyUser = new TimeOffPolicyUser();
    timeOffPolicyUser.setUserId(resultSet.getBinaryStream("user_id"));
    timeOffPolicyUser.setTimeOffPolicyId(resultSet.getBinaryStream("time_off_policy_id"));
    timeOffPolicyUser.setInitialBalance(resultSet.getInt("initial_balance"));
    return timeOffPolicyUser;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, user_id, time_off_policy_id, "
            + "initial_balance, created_at, updated_at) "
            + "VALUES(unhex(?), unhex(?), ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, TimeOffPolicyUser entity)
      throws Exception {
    byte[] timeOffPolicyIdBytes = IOUtils.toByteArray(entity.getId());
    UUID timeOffPolicyIdUUID = UuidUtils.fromBytes(timeOffPolicyIdBytes);
    byte[] userIdBytes = IOUtils.toByteArray(entity.getUserId());
    UUID userIdUUID = UuidUtils.fromBytes(userIdBytes);
    log.info("starting buildInsertStatement: id={}, userId={}", timeOffPolicyIdUUID.toString(), userIdUUID.toString());
    statement.setObject(1, timeOffPolicyIdUUID.toString().replace("-",""));
    statement.setObject(2, userIdUUID.toString().replace("-",""));
    statement.setBinaryStream(3, entity.getTimeOffPolicyId());
    statement.setInt(4, entity.getInitialBalance());
    statement.setTimestamp(5, entity.getCreatedAt());
    statement.setTimestamp(6, entity.getUpdatedAt());
  }
}
