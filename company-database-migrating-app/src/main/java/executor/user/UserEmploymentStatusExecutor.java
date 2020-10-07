package executor.user;

import entity.user.UserEmploymentStatus;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserEmploymentStatusExecutor extends BaseExecutor<UserEmploymentStatus> {

  private UserEmploymentStatusExecutor(final String companyId) {
    super(companyId, "user_employment_statuses");
  }

  public static UserEmploymentStatusExecutor getInstance(final String companyId) {
    return new UserEmploymentStatusExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String sql =
        "SELECT ues.* FROM %s ues "
            + "LEFT JOIN users u ON u.id = ues.user_id "
            + "WHERE u.company_id = unhex(?)";
    return String.format(sql, tableName);
  }

  @Override
  protected UserEmploymentStatus buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final UserEmploymentStatus userEmploymentStatus = new UserEmploymentStatus();
    userEmploymentStatus.setEffectiveDate(resultSet.getTimestamp("effective_date"));
    userEmploymentStatus.setUserEmploymentStatusTypeId(
        resultSet.getBinaryStream("user_employment_status_type_id"));
    userEmploymentStatus.setComment(resultSet.getString("comment"));
    userEmploymentStatus.setUserId(resultSet.getBinaryStream("user_id"));
    return userEmploymentStatus;
  }

  @Override
  protected String getInsertSql() {
    final String sql =
        "INSERT INTO (id, effective_date, user_employment_status_type_id, "
            + "comment, user_id, created_at, "
            + "updated_at) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, UserEmploymentStatus entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setTimestamp(2, entity.getEffectiveDate());
    statement.setBinaryStream(3, entity.getUserEmploymentStatusTypeId());
    statement.setString(4, entity.getComment());
    statement.setBinaryStream(5, entity.getUserId());
    statement.setTimestamp(6, entity.getCreatedAt());
    statement.setTimestamp(7, entity.getUpdatedAt());
  }
}
