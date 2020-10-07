package executor.user;

import entity.user.CompensationChangeReason;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CompensationChangeReasonExecutor extends BaseExecutor<CompensationChangeReason> {

  private CompensationChangeReasonExecutor(final String companyId) {
    super(companyId, "compensation_change_reasons");
  }

  public static CompensationChangeReasonExecutor getInstance(final String companyId) {
    return new CompensationChangeReasonExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql = "SELECT * FROM %s " + "WHERE company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected CompensationChangeReason buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final CompensationChangeReason compensationChangeReason = new CompensationChangeReason();
    compensationChangeReason.setName(resultSet.getString("name"));
    return compensationChangeReason;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, name, created_at, " + "updated_at) " + "VALUES(?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, CompensationChangeReason entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setString(2, entity.getName());
    statement.setTimestamp(3, entity.getCreatedAt());
    statement.setTimestamp(4, entity.getUpdatedAt());
  }
}
