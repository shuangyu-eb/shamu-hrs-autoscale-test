package executor.company;

import entity.company.Office;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OfficeExecutor extends BaseExecutor<Office> {

  private OfficeExecutor(final String companyId) {
    super(companyId, "offices");
  }

  public static OfficeExecutor getInstance(final String companyId) {
    return new OfficeExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql = "SELECT * FROM %s WHERE company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected Office buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final Office office = new Office();
    office.setOfficeId(resultSet.getBinaryStream("office_id"));
    office.setName(resultSet.getString("name"));
    office.setPhone(resultSet.getString("phone"));
    office.setEmail(resultSet.getString("email"));
    office.setOfficeAddressId(resultSet.getBinaryStream("office_address_id"));
    return office;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, office_id, name, "
            + "phone, email, office_address_id, "
            + "created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, Office entity) throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getOfficeId());
    statement.setString(3, entity.getName());
    statement.setString(4, entity.getPhone());
    statement.setString(5, entity.getEmail());
    statement.setBinaryStream(6, entity.getOfficeAddressId());
    statement.setTimestamp(7, entity.getCreatedAt());
    statement.setTimestamp(8, entity.getUpdatedAt());
  }
}
