package executor.user;

import entity.user.DismissedAt;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DismissedAtExecutor extends BaseExecutor<DismissedAt> {

  private DismissedAtExecutor(final String companyId) {
    super(companyId, "dismissed_at");
  }

  public static DismissedAtExecutor getInstance(final String companyId) {
    return new DismissedAtExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT da.* FROM %s da "
            + "LEFT JOIN users u ON u.id = da.user_id "
            + "WHERE u.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected DismissedAt buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final DismissedAt dismissedAt = new DismissedAt();
    dismissedAt.setUserId(resultSet.getBinaryStream("user_id"));
    dismissedAt.setSystemAnnouncementId(resultSet.getBinaryStream("system_announcement_id"));
    return dismissedAt;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, user_id, system_announcement_id, "
            + "created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, DismissedAt entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getUserId());
    statement.setBinaryStream(3, entity.getSystemAnnouncementId());
    statement.setTimestamp(4, entity.getCreatedAt());
    statement.setTimestamp(5, entity.getUpdatedAt());
  }
}
