package executor.benefit;

import entity.benefit.BenefitPlanUser;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BenefitPlanUserExecutor extends BaseExecutor<BenefitPlanUser> {
  private BenefitPlanUserExecutor(final String companyId) {
    super(companyId, "benefit_plans_users");
  }

  public static BenefitPlanUserExecutor getInstance(final String companyId) {
    return new BenefitPlanUserExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT bpu.* FROM %s bpu "
            + "LEFT JOIN benefit_plans bp ON bpu.benefit_plan_id = bp.id "
            + "WHERE bp.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected BenefitPlanUser buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final BenefitPlanUser benefitPlanUser = new BenefitPlanUser();
    benefitPlanUser.setBenefitPlanId(resultSet.getBinaryStream("benefit_plan_id"));
    benefitPlanUser.setUserId(resultSet.getBinaryStream("user_id"));
    benefitPlanUser.setEnrolled(resultSet.getBoolean("enrolled"));
    benefitPlanUser.setCoverageId(resultSet.getBinaryStream("coverage_id"));
    benefitPlanUser.setDependentId(resultSet.getBinaryStream("dependent_id"));
    benefitPlanUser.setConfirmed(resultSet.getBoolean("confirmed"));
    return benefitPlanUser;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, benefit_plan_id, user_id, "
            + "created_at, updated_at, enrolled, "
            + "coverage_id, dependent_id, confirmed) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, BenefitPlanUser entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getBenefitPlanId());
    statement.setBinaryStream(3, entity.getUserId());
    statement.setTimestamp(4, entity.getCreatedAt());
    statement.setTimestamp(5, entity.getUpdatedAt());
    statement.setBoolean(6, entity.getEnrolled());
    statement.setBinaryStream(7, entity.getCoverageId());
    statement.setBinaryStream(8, entity.getDependentId());
    statement.setBoolean(9, entity.getConfirmed());
  }
}
