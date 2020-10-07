package executor.attendance;

import entity.attendance.PayrollDetail;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PayrollDetailExecutor extends BaseExecutor<PayrollDetail> {

  private PayrollDetailExecutor(final String companyId) {
    super(companyId, "payroll_details");
  }

  public static PayrollDetailExecutor getInstance(final String companyId) {
    return new PayrollDetailExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String sql = "SELECT * FROM %s WHERE company_id = unhex(?)";
    return String.format(sql, tableName);
  }

  @Override
  protected PayrollDetail buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final PayrollDetail payrollDetail = new PayrollDetail();
    payrollDetail.setLastPayrollPayday(resultSet.getTimestamp("last_payroll_payday"));
    payrollDetail.setPayFrequencyTypeId(resultSet.getBinaryStream("pay_frequency_type_id"));
    return payrollDetail;
  }

  @Override
  protected String getInsertSql() {
    final String sql =
        "INSERT INTO %s (id, pay_frequency_type_id, last_payroll_payday, "
            + "created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, PayrollDetail entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(
        2,
        getRelatedForeignKeyByRefId(
            "static_company_pay_frequency_types", entity.getPayFrequencyTypeId()));
    statement.setTimestamp(3, entity.getLastPayrollPayday());
    statement.setTimestamp(4, entity.getCreatedAt());
    statement.setTimestamp(5, entity.getUpdatedAt());
  }
}
