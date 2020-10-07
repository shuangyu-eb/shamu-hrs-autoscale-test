package executor.benefit;

import entity.benefit.BenefitRequest;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BenefitRequestExecutor extends BaseExecutor<BenefitRequest> {
  private BenefitRequestExecutor(final String companyId) {
    super(companyId, "benefits_requests");
  }

  public static BenefitRequestExecutor getInstance(final String companyId) {
    return new BenefitRequestExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT * FROM %s br "
            + "LEFT JOIN users u ON br.request_user_id = u.id "
            + "WHERE u.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected BenefitRequest buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final BenefitRequest benefitRequest = new BenefitRequest();
    benefitRequest.setRequestUserId(resultSet.getBinaryStream("request_user_id"));
    benefitRequest.setApproverUserId(resultSet.getBinaryStream("approver_user_id"));
    benefitRequest.setLifeEventTypeId(resultSet.getBinaryStream("life_event_type_id"));
    benefitRequest.setLifeEventDate(resultSet.getTimestamp("life_event_date"));
    benefitRequest.setPrevEnrollmentId(resultSet.getBinaryStream("prev_enrollment_id"));
    benefitRequest.setNextEnrollmentId(resultSet.getBinaryStream("next_enrollment_id"));
    benefitRequest.setEffectiveDate(resultSet.getTimestamp("effective_date"));
    benefitRequest.setRequestStatusId(resultSet.getBinaryStream("request_status_id"));
    return benefitRequest;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, request_user_id, approver_user_id, "
            + "life_event_type_id, life_event_date, prev_enrollment_id, "
            + "next_enrollment_id, effective_date, request_status_id, "
            + "created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, BenefitRequest entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getRequestUserId());
    statement.setBinaryStream(3, entity.getApproverUserId());
    statement.setBinaryStream(4, entity.getLifeEventTypeId());
    statement.setTimestamp(5, entity.getLifeEventDate());
    statement.setBinaryStream(6, entity.getPrevEnrollmentId());
    statement.setBinaryStream(7, entity.getNextEnrollmentId());
    statement.setTimestamp(8, entity.getEffectiveDate());
    statement.setBinaryStream(9, entity.getRequestStatusId());
    statement.setTimestamp(10, entity.getCreatedAt());
    statement.setTimestamp(11, entity.getUpdatedAt());
  }
}
