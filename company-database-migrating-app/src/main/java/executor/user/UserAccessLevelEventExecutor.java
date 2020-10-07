package executor.user;

import entity.user.UserAccessLevelEvent;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserAccessLevelEventExecutor extends BaseExecutor<UserAccessLevelEvent> {

  private UserAccessLevelEventExecutor(final String companyId) {
    super(companyId, "user_access_level_events");
  }

  public static UserAccessLevelEventExecutor getInstance(final String companyId) {
    return new UserAccessLevelEventExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String sql =
        "SELECT uale.* FROM %s uale "
            + "LEFT JOIN users u ON u.id = uale.user_id "
            + "WHERE u.company_id = unhex(?)";
    return String.format(sql, tableName);
  }

  @Override
  protected UserAccessLevelEvent buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final UserAccessLevelEvent u = new UserAccessLevelEvent();
    u.setUserId(resultSet.getBinaryStream("user_id"));
    u.setOriginalRole(resultSet.getString("original_role"));
    return u;
  }

  @Override
  protected String getInsertSql() {
    final String sql =
        "INSERT INTO %s (id, user_id, original_role, "
            + "created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, UserAccessLevelEvent entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getUserId());
    statement.setString(3, entity.getOriginalRole());
    statement.setTimestamp(4, entity.getCreatedAt());
    statement.setTimestamp(5, entity.getUpdatedAt());
  }
}
