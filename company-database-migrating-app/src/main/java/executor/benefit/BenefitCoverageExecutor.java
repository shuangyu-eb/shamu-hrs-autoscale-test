package executor.benefit;

import entity.benefit.BenefitCoverage;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BenefitCoverageExecutor extends BaseExecutor<BenefitCoverage> {
  private BenefitCoverageExecutor(final String companyId) {
    super(companyId, "benefit_coverages");
  }

  public static BenefitCoverageExecutor getInstance(final String companyId) {
    return new BenefitCoverageExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT bc.* FROM %s bc "
            + "LEFT JOIN benefit_plans bp ON bc.benefit_plan_id = bp.id "
            + "WHERE bp.company_id = unhex(?) "
            // Table 'BenefitCoverages' contains both static(created by Liquibase from csv file) and custom(created by users) data.
            // The 'ref_id' of users' data is '0' or 'NULL'.
            + "AND (bc.ref_id = 0 OR bc.ref_id IS NULL)";
    return String.format(querySql, tableName);
  }

  @Override
  protected BenefitCoverage buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final BenefitCoverage benefitCoverage = new BenefitCoverage();
    benefitCoverage.setName(resultSet.getString("name"));
    benefitCoverage.setRefId(resultSet.getInt("ref_id"));
    benefitCoverage.setBenefitPlanId(resultSet.getBinaryStream("benefit_plan_id"));
    return benefitCoverage;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, name, benefit_plan_id, "
            + "ref_id, created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, BenefitCoverage entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setString(2, entity.getName());
    statement.setBinaryStream(3, entity.getBenefitPlanId());
    statement.setInt(4, entity.getRefId());
    statement.setTimestamp(5, entity.getCreatedAt());
    statement.setTimestamp(6, entity.getUpdatedAt());
  }
}
