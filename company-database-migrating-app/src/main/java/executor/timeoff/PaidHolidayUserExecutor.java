package executor.timeoff;

import entity.timeoff.PaidHolidayUser;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PaidHolidayUserExecutor extends BaseExecutor<PaidHolidayUser> {

  private PaidHolidayUserExecutor(final String companyId) {
    super(companyId, "paid_holidays_users");
  }

  public static PaidHolidayUserExecutor getInstance(final String companyId) {
    return new PaidHolidayUserExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT phu.* FROM %s phu "
            + "LEFT JOIN users u ON u.id = phu.user_id "
            + "WHERE u.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected PaidHolidayUser buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final PaidHolidayUser paidHolidayUser = new PaidHolidayUser();
    paidHolidayUser.setUserId(resultSet.getBinaryStream("user_id"));
    paidHolidayUser.setIsSelected(resultSet.getBoolean("is_selected"));
    return paidHolidayUser;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, user_id, is_selected, "
            + "created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, PaidHolidayUser entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getUserId());
    statement.setBoolean(3, entity.getIsSelected());
    statement.setTimestamp(4, entity.getCreatedAt());
    statement.setTimestamp(5, entity.getUpdatedAt());
  }
}
