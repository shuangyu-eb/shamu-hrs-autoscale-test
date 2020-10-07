package executor.benefit;

import entity.benefit.BenefitSetting;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BenefitSettingExecutor extends BaseExecutor<BenefitSetting> {
  private BenefitSettingExecutor(final String companyId) {
    super(companyId, "company_benefits_setting");
    this.tableFullName = schemaName + "." + "benefits_setting";
  }

  public static BenefitSettingExecutor getInstance(final String companyId) {
    return new BenefitSettingExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String sql = "SELECT * FROM %s WHERE company_id = unhex(?)";
    return String.format(sql, tableName);
  }

  @Override
  protected BenefitSetting buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final BenefitSetting benefitSetting = new BenefitSetting();
    benefitSetting.setAutomaticRollover(resultSet.getBoolean("automatic_rollover"));
    benefitSetting.setPeriodEndDate(resultSet.getTimestamp("period_end_date"));
    benefitSetting.setPeriodStartDate(resultSet.getTimestamp("period_start_date"));
    return benefitSetting;
  }

  @Override
  protected String getInsertSql() {
    final String sql = "INSERT INTO %s (id, automatic_rollover, period_end_date, " +
      "period_start_date, created_at, updated_at) " +
      "VALUES(?, ?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, BenefitSetting entity) throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBoolean(2, entity.getAutomaticRollover());
    statement.setTimestamp(3, entity.getPeriodEndDate());
    statement.setTimestamp(4, entity.getPeriodStartDate());
    statement.setTimestamp(5, entity.getCreatedAt());
    statement.setTimestamp(6, entity.getUpdatedAt());
  }
}
