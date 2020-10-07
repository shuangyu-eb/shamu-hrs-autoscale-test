package executor.benefit;

import entity.benefit.RetirementPlanType;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RetirementPlanTypeExecutor extends BaseExecutor<RetirementPlanType> {
  private RetirementPlanTypeExecutor(final String companyId) {
    super(companyId, "retirement_plans_types");
  }

  public static RetirementPlanTypeExecutor getInstance(final String companyId) {
    return new RetirementPlanTypeExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT rpt.* FROM %s rpt "
            + "LEFT JOIN benefit_plans bp ON rpt.benefit_plan_id = bp.id "
            + "WHERE bp.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected RetirementPlanType buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final RetirementPlanType retirementPlanType = new RetirementPlanType();
    retirementPlanType.setBenefitPlanId(resultSet.getBinaryStream("benefit_plan_id"));
    retirementPlanType.setRetirementTypeId(resultSet.getBinaryStream("retirement_type_id"));
    return retirementPlanType;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, benefit_plan_id, retirement_type_id, "
            + "created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, RetirementPlanType entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getBenefitPlanId());
    statement.setBinaryStream(
        3, getRelatedForeignKeyByRefId("retirement_types", entity.getRetirementTypeId()));
    statement.setTimestamp(4, entity.getCreatedAt());
    statement.setTimestamp(5, entity.getUpdatedAt());
  }
}
