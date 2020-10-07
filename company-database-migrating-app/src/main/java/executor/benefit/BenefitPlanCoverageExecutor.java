package executor.benefit;

import entity.benefit.BenefitPlanCoverage;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BenefitPlanCoverageExecutor extends BaseExecutor<BenefitPlanCoverage> {
  private BenefitPlanCoverageExecutor(final String companyId) {
    super(companyId, "benefit_plan_coverages");
  }

  public static BenefitPlanCoverageExecutor getInstance(final String companyId) {
    return new BenefitPlanCoverageExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT bpc.*, bc.ref_id FROM %s bpc "
            + "LEFT JOIN benefit_coverages bc ON bpc.benefit_coverage_id = bc.id "
            + "WHERE bpc.benefit_plan_id IN ("
            + "SELECT bp.id FROM benefit_plans bp WHERE bp.company_id = unhex(?))";
    return String.format(querySql, tableName);
  }

  @Override
  protected BenefitPlanCoverage buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final BenefitPlanCoverage benefitPlanCoverage = new BenefitPlanCoverage();
    benefitPlanCoverage.setBenefitPlanId(resultSet.getBinaryStream("benefit_plan_id"));
    benefitPlanCoverage.setEmployeeCost(resultSet.getBigDecimal("employee_cost"));
    benefitPlanCoverage.setEmployerCost(resultSet.getBigDecimal("employer_cost"));
    benefitPlanCoverage.setBenefitCoverageId(resultSet.getBinaryStream("benefit_coverage_id"));
    benefitPlanCoverage.setRefId(resultSet.getInt("ref_id"));
    return benefitPlanCoverage;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, benefit_plan_id, created_at, "
            + "updated_at, employer_cost, employee_cost, "
            + "benefit_coverage_id) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, BenefitPlanCoverage entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getBenefitPlanId());
    statement.setTimestamp(3, entity.getCreatedAt());
    statement.setTimestamp(4, entity.getUpdatedAt());
    statement.setBigDecimal(5, entity.getEmployerCost());
    statement.setBigDecimal(6, entity.getEmployeeCost());
    // Custom benefit coverages inserted by employees
    if (entity.getRefId() == 0 || entity.getRefId() == null) {
      statement.setBinaryStream(7, entity.getBenefitCoverageId());
    } else {
      // Pre-created benefit coverages created by liquibase
      statement.setBinaryStream(
          7, getRelatedForeignKeyByRefId("benefit_coverages", entity.getBenefitCoverageId()));
    }
  }
}
