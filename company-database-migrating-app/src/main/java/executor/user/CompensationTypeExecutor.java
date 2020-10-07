package executor.user;

import entity.user.CompensationType;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CompensationTypeExecutor extends BaseExecutor<CompensationType> {

  private CompensationTypeExecutor(final String companyId) {
    super(companyId, "compensation_types");
  }

  public static CompensationTypeExecutor getInstance(final String companyId) {
    return new CompensationTypeExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql = "SELECT * FROM %s "
        + "WHERE company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected CompensationType buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final CompensationType compensationType = new CompensationType();
    compensationType.setName(resultSet.getString("name"));
    return compensationType;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql = "INSERT INTO %s (id, name, created_at, "
        + "updated_at) "
        + "VALUES(?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, CompensationType entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setString(2, entity.getName());
    statement.setTimestamp(3, entity.getCreatedAt());
    statement.setTimestamp(4, entity.getUpdatedAt());
  }
}
