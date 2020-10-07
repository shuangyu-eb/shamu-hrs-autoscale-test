package executor.timeoff;

import entity.timeoff.TimeOffRequestComment;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TimeOffRequestCommentExecutor extends BaseExecutor<TimeOffRequestComment> {

  private TimeOffRequestCommentExecutor(final String companyId) {
    super(companyId, "time_off_request_comments");
  }

  public static TimeOffRequestCommentExecutor getInstance(final String companyId) {
    return new TimeOffRequestCommentExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT * FROM %s trc "
            + "LEFT JOIN users u ON trc.user_id = u.id "
            + "WHERE u.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected TimeOffRequestComment buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final TimeOffRequestComment timeOffRequestComment = new TimeOffRequestComment();
    timeOffRequestComment.setTimeOffRequestId(resultSet.getBinaryStream("time_off_request_id"));
    timeOffRequestComment.setUserId(resultSet.getBinaryStream("user_id"));
    timeOffRequestComment.setComment(resultSet.getString("comment"));
    return timeOffRequestComment;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, time_off_request_id, user_id, "
            + "comment, created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, TimeOffRequestComment entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getTimeOffRequestId());
    statement.setBinaryStream(3, entity.getUserId());
    statement.setString(4, entity.getComment());
    statement.setTimestamp(5, entity.getCreatedAt());
    statement.setTimestamp(6, entity.getUpdatedAt());
  }
}
