package executor.timeoff;

import entity.timeoff.PaidHoliday;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PaidHolidayExecutor extends BaseExecutor<PaidHoliday> {

  private PaidHolidayExecutor(final String companyId) {
    super(companyId, "paid_holidays");
  }

  public static PaidHolidayExecutor getInstance(final String companyId) {
    return new PaidHolidayExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String sql = "SELECT * FROM %s " + "WHERE company_id=unhex(?)";
    return String.format(sql, tableName);
  }

  @Override
  protected PaidHoliday buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final PaidHoliday paidHoliday = new PaidHoliday();
    paidHoliday.setCountryId(resultSet.getBinaryStream("country_id"));
    paidHoliday.setName(resultSet.getString("name"));
    paidHoliday.setDate(resultSet.getTimestamp("date"));
    paidHoliday.setNameShow(resultSet.getString("name_show"));
    paidHoliday.setFederal(resultSet.getBoolean("federal"));
    paidHoliday.setCreatorId(resultSet.getBinaryStream("creator_id"));
    return paidHoliday;
  }

  @Override
  protected String getInsertSql() {
    final String sql =
        "INSERT INTO %s (id, country_id, name, "
            + "created_at, updated_at, date, "
            + "name_show, federal, creator_id) "
            + "VALUES(?, ?, ? ,?, ?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, PaidHoliday entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getCountryId());
    statement.setString(3, entity.getName());
    statement.setTimestamp(4, entity.getCreatedAt());
    statement.setTimestamp(5, entity.getUpdatedAt());
    statement.setTimestamp(6, entity.getDate());
    statement.setString(7, entity.getNameShow());
    statement.setBoolean(8, entity.getFederal());
    statement.setBinaryStream(9, entity.getCreatorId());
  }
}
