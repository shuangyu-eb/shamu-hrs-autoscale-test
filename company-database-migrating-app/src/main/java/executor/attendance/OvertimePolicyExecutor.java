package executor.attendance;

import entity.attendance.OvertimePolicy;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OvertimePolicyExecutor extends BaseExecutor<OvertimePolicy> {
  private OvertimePolicyExecutor(final String companyId) {
    super(companyId, "overtime_policies");
  }

  public static OvertimePolicyExecutor getInstance(final String companyId) {
    return new OvertimePolicyExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String sql = "SELECT * FROM %s WHERE company_id = unhex(?)";
    return String.format(sql, tableName);
  }

  @Override
  protected OvertimePolicy buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final OvertimePolicy overtimePolicy = new OvertimePolicy();
    overtimePolicy.setActive(resultSet.getBoolean("active"));
    overtimePolicy.setDefaultPolicy(resultSet.getBoolean("default_policy"));
    overtimePolicy.setPolicyName(resultSet.getString("policy_name"));
    return overtimePolicy;
  }

  @Override
  protected String getInsertSql() {
    final String sql= "INSERT INTO %s (id, policy_name, "
        + "default_policy, active, created_at, "
        + "updated_at) "
        + "VALUES(?, ?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, OvertimePolicy entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setString(2, entity.getPolicyName());
    statement.setBoolean(3, entity.getDefaultPolicy());
    statement.setBoolean(4, entity.getActive());
    statement.setTimestamp(5, entity.getCreatedAt());
    statement.setTimestamp(6, entity.getUpdatedAt());
  }
}
