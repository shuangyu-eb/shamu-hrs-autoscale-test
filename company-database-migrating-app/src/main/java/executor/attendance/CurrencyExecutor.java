package executor.attendance;

import entity.attendance.Currency;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CurrencyExecutor extends BaseExecutor<Currency> {

  private CurrencyExecutor(final String companyId) {
    super(companyId, "currencies");
  }

  public static CurrencyExecutor getInstance(final String companyId) {
    return new CurrencyExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT c.* FROM %s c "
            + "LEFT JOIN user_compensations uc ON uc.currency_id = c.id "
            + "LEFT JOIN users u ON u.id = uc.user_id "
            + "WHERE u.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected Currency buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final Currency currency = new Currency();
    currency.setName(resultSet.getString("name"));
    currency.setAbbreviation(resultSet.getString("abbreviation"));
    return currency;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, name, abbreviation, "
            + "created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, Currency entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setString(2, entity.getName());
    statement.setString(3, entity.getAbbreviation());
    statement.setTimestamp(4, entity.getCreatedAt());
    statement.setTimestamp(5, entity.getUpdatedAt());
  }
}
