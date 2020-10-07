package executor.attendance;

import entity.attendance.PolicyDetail;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PolicyDetailExecutor extends BaseExecutor<PolicyDetail> {

  private PolicyDetailExecutor(final String companyId) {
    super(companyId, "policy_details");
  }

  public static PolicyDetailExecutor getInstance(final String companyId) {
    return new PolicyDetailExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String sql = "SELECT pd.* FROM %s pd "
        + "LEFT JOIN overtime_policies op ON pd.overtime_policy_id = op.id "
        + "WHERE op.company_id = unhex(?)";
    return String.format(sql, tableName);
  }

  @Override
  protected PolicyDetail buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final PolicyDetail policyDetail = new PolicyDetail();
    policyDetail.setOvertimePolicyId(resultSet.getBinaryStream("overtime_policy_id"));
    policyDetail.setOvertimeTypeId(getRelatedForeignKeyByRefId("static_overtime_types", resultSet.getBinaryStream("overtime_type_id")));
    policyDetail.setStart(resultSet.getInt("start"));
    policyDetail.setRate(resultSet.getBigDecimal("rate"));
    return policyDetail;
  }

  @Override
  protected String getInsertSql() {
    final String sql = "INSERT INTO %s (id, start, overtime_type_id, "
        + "rate, created_at, updated_at, "
        + "overtime_policy_id) "
        + "VALUES(?, ?, ?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, PolicyDetail entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setInt(2, entity.getStart());
    statement.setBinaryStream(3, entity.getOvertimeTypeId());
    statement.setBigDecimal(4, entity.getRate());
    statement.setTimestamp(5, entity.getCreatedAt());
    statement.setTimestamp(6, entity.getUpdatedAt());
    statement.setBinaryStream(7, entity.getOvertimePolicyId());
  }
}
