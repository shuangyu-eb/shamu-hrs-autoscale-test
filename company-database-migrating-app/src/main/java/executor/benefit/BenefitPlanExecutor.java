package executor.benefit;

import entity.benefit.BenefitPlan;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BenefitPlanExecutor extends BaseExecutor<BenefitPlan> {
  private BenefitPlanExecutor(final String companyId) {
    super(companyId, "benefit_plans");
  }

  public static BenefitPlanExecutor getInstance(final String companyId) {
    return new BenefitPlanExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT * "
            + "FROM %s bp "
            + "LEFT JOIN benefit_plan_types bpt ON bp.benefit_plan_type_id = bpt.id "
            + "WHERE company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected BenefitPlan buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final BenefitPlan benefitPlan = new BenefitPlan();
    benefitPlan.setDescription(resultSet.getString("description"));
    benefitPlan.setEndDate(resultSet.getTimestamp("end_date"));
    benefitPlan.setStartDate(resultSet.getTimestamp("start_date"));
    benefitPlan.setImageUrl(resultSet.getString("image_url"));
    benefitPlan.setName(resultSet.getString("name"));
    benefitPlan.setWebsite(resultSet.getString("website"));
    benefitPlan.setPlanId(resultSet.getString("plan_id"));
    benefitPlan.setBenefitPlanTypeId(resultSet.getBinaryStream("benefit_plan_type_id"));
    return benefitPlan;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, name, image_url, "
            + "website, benefit_plan_type_id, created_at, "
            + "updated_at, plan_id, start_date, "
            + "end_date, description) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, BenefitPlan entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setString(2, entity.getName());
    statement.setString(3, entity.getImageUrl());
    statement.setString(4, entity.getWebsite());
    statement.setBinaryStream(
        5, getRelatedForeignKeyByRefId("benefit_plan_types", entity.getBenefitPlanTypeId()));
    statement.setTimestamp(6, entity.getCreatedAt());
    statement.setTimestamp(7, entity.getUpdatedAt());
    statement.setString(8, entity.getPlanId());
    statement.setTimestamp(9, entity.getStartDate());
    statement.setTimestamp(10, entity.getEndDate());
    statement.setString(11, entity.getDescription());
  }
}
