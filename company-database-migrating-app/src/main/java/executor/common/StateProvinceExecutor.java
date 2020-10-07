package executor.common;

import entity.common.StateProvince;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StateProvinceExecutor extends BaseExecutor<StateProvince> {

  private StateProvinceExecutor(final String companyId) {
    super(companyId, "states_provinces");
    companyIdRequired = Boolean.FALSE;
  }

  public static StateProvinceExecutor getInstance(final String companyId) {
    return new StateProvinceExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql = "SELECT * FROM %s ";
    return String.format(querySql, tableName);
  }

  @Override
  protected StateProvince buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final StateProvince stateProvince = new StateProvince();
    stateProvince.setName(resultSet.getString("name"));
    stateProvince.setCountryId(resultSet.getBinaryStream("country_id"));
    return stateProvince;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, country_id, name, "
            + "created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, StateProvince entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, getRelatedForeignKeyByRefId("countries", entity.getCountryId()));
    statement.setString(3, entity.getName());
    statement.setTimestamp(4, entity.getCreatedAt());
    statement.setTimestamp(5, entity.getUpdatedAt());
  }
}
