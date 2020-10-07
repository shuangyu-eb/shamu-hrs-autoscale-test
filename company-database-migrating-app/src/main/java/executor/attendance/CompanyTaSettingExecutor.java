package executor.attendance;

import entity.attendance.CompanyTaSetting;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CompanyTaSettingExecutor extends BaseExecutor<CompanyTaSetting> {

  private CompanyTaSettingExecutor(final String companyId) {
    super(companyId, "company_ta_settings");
  }

  public static CompanyTaSettingExecutor getInstance(final String companyId) {
    return new CompanyTaSettingExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql = "SELECT * FROM %s WHERE company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected CompanyTaSetting buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final CompanyTaSetting companyTaSetting = new CompanyTaSetting();
    companyTaSetting.setTimeZoneId(resultSet.getBinaryStream("time_zone_id"));
    companyTaSetting.setApprovalDaysBeforePayroll(resultSet.getInt("approval_days_before_payroll"));
    companyTaSetting.setStartOfWeek(resultSet.getString("start_of_week"));
    companyTaSetting.setMessagingOn(resultSet.getInt("messaging_on"));
    companyTaSetting.setOvertimeAlert(resultSet.getInt("overtime_alert"));
    return companyTaSetting;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, time_zone_id, "
            + "approval_days_before_payroll, created_at, "
            + "messaging_on, updated_at, start_of_week, "
            + "overtime_alert) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, CompanyTaSetting entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(
        2, getRelatedForeignKeyByRefId("static_timezones", entity.getTimeZoneId()));
    statement.setInt(3, entity.getApprovalDaysBeforePayroll());
    statement.setTimestamp(4, entity.getCreatedAt());
    statement.setInt(5, entity.getMessagingOn());
    statement.setTimestamp(6, entity.getUpdatedAt());
    statement.setString(7, entity.getStartOfWeek());
    statement.setInt(8, entity.getOvertimeAlert());
  }
}
