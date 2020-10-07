package executor.company;

import entity.company.OfficeAddress;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OfficeAddressExecutor extends BaseExecutor<OfficeAddress> {

  private OfficeAddressExecutor(final String companyId) {
    super(companyId, "office_addresses");
  }

  public static OfficeAddressExecutor getInstance(final String companyId) {
    return new OfficeAddressExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT oa.* FROM %s oa "
            + "LEFT JOIN offices o ON oa.id = o.office_address_id "
            + "WHERE o.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected OfficeAddress buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final OfficeAddress officeAddress = new OfficeAddress();
    officeAddress.setStreet1(resultSet.getString("street_1"));
    officeAddress.setStreet2(resultSet.getString("street_2"));
    officeAddress.setStateProvinceId(resultSet.getBinaryStream("state_province_id"));
    officeAddress.setPostalCode(resultSet.getString("postal_code"));
    officeAddress.setCity(resultSet.getString("city"));
    return officeAddress;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, street_1, street_2, "
            + "state_province_id, postal_code, created_at, "
            + "updated_at, city) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, OfficeAddress entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setString(2, entity.getStreet1());
    statement.setString(3, entity.getStreet2());
    statement.setBinaryStream(4, getRelatedForeignKeyByName("states_provinces", entity.getStateProvinceId()));
    statement.setString(5, entity.getPostalCode());
    statement.setTimestamp(6, entity.getCreatedAt());
    statement.setTimestamp(7, entity.getUpdatedAt());
    statement.setString(8, entity.getCity());
  }
}
