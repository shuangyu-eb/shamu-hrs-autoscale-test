package executor.benefit;

import entity.benefit.BenefitPlanDependent;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BenefitPlanDependentExecutor extends BaseExecutor<BenefitPlanDependent> {

  private BenefitPlanDependentExecutor(final String companyId) {
    super(companyId, "benefit_plan_dependents");
  }

  public static BenefitPlanDependentExecutor getInstance(final String companyId) {
    return new BenefitPlanDependentExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT bpd.* FROM %s bpd "
            + "LEFT JOIN benefit_plans_users bpu ON bpd.benefit_plans_users_id = bpu.id "
            + "LEFT JOIN benefit_plans bp ON bpu.benefit_plan_id = bp.id "
            + "WHERE bp.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected BenefitPlanDependent buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final BenefitPlanDependent benefitPlanDependent = new BenefitPlanDependent();
    benefitPlanDependent.setBenefitPlansUsersId(
        resultSet.getBinaryStream("benefit_plans_users_id"));
    benefitPlanDependent.setUserDependentsId(resultSet.getBinaryStream("user_dependents_id"));
    return benefitPlanDependent;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, benefit_plans_users_id, user_dependents_id, "
            + "created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, BenefitPlanDependent entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getBenefitPlansUsersId());
    statement.setBinaryStream(3, entity.getUserDependentsId());
    statement.setTimestamp(4, entity.getCreatedAt());
    statement.setTimestamp(5, entity.getUpdatedAt());
  }
}
