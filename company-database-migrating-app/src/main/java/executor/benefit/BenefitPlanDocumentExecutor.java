package executor.benefit;

import entity.benefit.BenefitPlanDocument;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BenefitPlanDocumentExecutor extends BaseExecutor<BenefitPlanDocument> {

  private BenefitPlanDocumentExecutor(final String companyId) {
    super(companyId, "benefit_plan_documents");
  }

  public static BenefitPlanDocumentExecutor getInstance(final String companyId) {
    return new BenefitPlanDocumentExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT bpd.* "
            + "FROM %s bpd "
            + "LEFT JOIN benefit_plans bp ON bpd.benefit_plan_id = bp.id "
            + "WHERE bp.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected BenefitPlanDocument buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final BenefitPlanDocument benefitPlanDocument = new BenefitPlanDocument();
    benefitPlanDocument.setUrl(resultSet.getString("url"));
    benefitPlanDocument.setTitle(resultSet.getString("title"));
    benefitPlanDocument.setBenefitPlanId(resultSet.getBinaryStream("benefit_plan_id"));
    return benefitPlanDocument;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, title, url, "
            + "benefit_plan_id, created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, BenefitPlanDocument entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setString(2, entity.getTitle());
    statement.setString(3, entity.getUrl());
    statement.setBinaryStream(4, entity.getBenefitPlanId());
    statement.setTimestamp(5, entity.getCreatedAt());
    statement.setTimestamp(6, entity.getUpdatedAt());
  }
}
