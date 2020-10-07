package executor.company;

import entity.company.Department;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DepartmentExecutor extends BaseExecutor<Department> {

  private DepartmentExecutor(final String companyId) {
    super(companyId, "departments");
  }

  public static DepartmentExecutor getInstance(final String companyId) {
    return new DepartmentExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql = "SELECT * FROM %s " + "WHERE company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected Department buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final Department department = new Department();
    department.setName(resultSet.getString("name"));
    return department;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql = "INSERT INTO %s (id, name, created_at, updated_at) VALUES(?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, Department entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setString(2, entity.getName());
    statement.setTimestamp(3, entity.getCreatedAt());
    statement.setTimestamp(4, entity.getUpdatedAt());
  }
}
