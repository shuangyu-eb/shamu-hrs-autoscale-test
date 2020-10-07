package executor.user;

import lombok.extern.slf4j.Slf4j;
import org.zeroturnaround.zip.commons.IOUtils;

import entity.user.UserCompensation;
import executor.BaseExecutor;
import util.UuidUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

@Slf4j
public class UserCompensationExecutor extends BaseExecutor<UserCompensation> {

  private UserCompensationExecutor(final String companyId) {
    super(companyId, "user_compensations");
  }

  public static UserCompensationExecutor getInstance(final String companyId) {
    return new UserCompensationExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT uc.* FROM %s uc "
            + "LEFT JOIN users u ON u.id = uc.user_id "
            + "WHERE u.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected UserCompensation buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final UserCompensation compensation = new UserCompensation();
    compensation.setUserId(resultSet.getBinaryStream("user_id"));
    compensation.setWageCents(resultSet.getInt("wage_cents"));
    compensation.setCompensationFrequencyId(
        getRelatedForeignKeyByRefId(
            "compensation_frequency", resultSet.getBinaryStream("compensation_frequency_id")));
    compensation.setCompensationTypeId(resultSet.getBinaryStream("compensation_type_id"));
    compensation.setStartDate(resultSet.getTimestamp("start_date"));
    compensation.setEndDate(resultSet.getTimestamp("end_date"));
    compensation.setOvertimeStatusId(
        getRelatedForeignKeyByName(
            "compensation_overtime_statuses", resultSet.getBinaryStream("overtime_status_id")));
    compensation.setCompensationChangeReasonId(
        resultSet.getBinaryStream("compensation_change_reason_id"));
    compensation.setComment(resultSet.getString("comment"));
    compensation.setCurrencyId(resultSet.getBinaryStream("currency_id"));
    compensation.setOvertimePolicyId(resultSet.getBinaryStream("overtime_policy_id"));
    return compensation;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, user_id, wage_cents, "
            + "compensation_frequency_id, compensation_type_id, start_date, "
            + "end_date, overtime_status_id, compensation_change_reason_id, "
            + "comment, created_at, updated_at, "
            + "currency_id, overtime_policy_id) "
            + "VALUES(unhex(?), unhex(?), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, UserCompensation entity)
      throws Exception {
    byte[] compensationIdBytes = IOUtils.toByteArray(entity.getId());
    UUID compensationIdUUID = UuidUtils.fromBytes(compensationIdBytes);
    byte[] userIdBytes = IOUtils.toByteArray(entity.getUserId());
    UUID userIdUUID = UuidUtils.fromBytes(userIdBytes);
    log.info("starting buildInsertStatement: id={}, userId={}", compensationIdUUID.toString(), userIdUUID.toString());
    statement.setObject(1, compensationIdUUID.toString().replace("-",""));
    statement.setObject(2, userIdUUID.toString().replace("-",""));
    statement.setInt(3, entity.getWageCents());
    statement.setBinaryStream(4, entity.getCompensationFrequencyId());
    statement.setBinaryStream(5, entity.getCompensationTypeId());
    statement.setTimestamp(6, entity.getStartDate());
    statement.setTimestamp(7, entity.getEndDate());
    statement.setBinaryStream(8, entity.getOvertimeStatusId());
    statement.setBinaryStream(9, entity.getCompensationChangeReasonId());
    statement.setString(10, entity.getComment());
    statement.setTimestamp(11, entity.getCreatedAt());
    statement.setTimestamp(12, entity.getUpdatedAt());
    statement.setBinaryStream(13, entity.getCurrencyId());
    statement.setBinaryStream(14, entity.getOvertimePolicyId());
  }
}
